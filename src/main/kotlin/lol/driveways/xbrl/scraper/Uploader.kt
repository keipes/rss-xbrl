package lol.driveways.xbrl.scraper

import java.util.concurrent.*
import java.util.logging.Logger
import com.amazonaws.services.lambda.runtime.Context


class Uploader(queue: BlockingQueue<Filing>, numThreads: Int) {

    companion object {
        val log: Logger = Logger.getLogger(Dynamo::class.java.simpleName)
    }

    val batchSize = 20
    val executor = Executors.newFixedThreadPool(numThreads+1)!!
    val sem = Semaphore(1)
    val batchQueue = LinkedBlockingQueue<List<Filing>>()

    init {
        sem.acquire()
        // monitor thread
        executor.submit({
            val batch = mutableListOf<Filing>()
            while(sem.availablePermits() == 0 || !queue.isEmpty()) {
                val filing = queue.poll(10, TimeUnit.MILLISECONDS)
                if (filing != null) {
                    batch.add(filing)
                }
                if (batch.size == batchSize) {
                    batchQueue.put(batch.toList())
                    batch.clear()
                }
            }
            if (batch.size > 0) {
                batchQueue.put(batch)
            }
            log.info("Monitor thread finished.")
        })
        // uploader threads
        (1..numThreads).forEach({
            executor.submit({
                val dynamo = Dynamo()
                while(sem.availablePermits() == 0 || !batchQueue.isEmpty()) {
                    val filings: List<Filing>? = batchQueue.poll(10, TimeUnit.MILLISECONDS)
                    if (filings != null) {
                        dynamo.writeFilings(filings)
                    }
                }
                log.info("Dynamo thread finished.")
            })
        })
    }


    fun signalInputClosed(context: Context?) {
        sem.release()
        executor.shutdown()
        val timeout: Number = context?.remainingTimeInMillis?.minus(50) ?: 10000
        executor.awaitTermination(timeout.toLong(), TimeUnit.MILLISECONDS)
        executor.shutdownNow()
    }
}