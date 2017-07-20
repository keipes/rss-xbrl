package lol.driveways.xbrl.scraper

import com.amazonaws.services.lambda.runtime.Context
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ArrayBlockingQueue
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader

class Edgar {

    fun scrape(context: Context?) {
        val start = System.currentTimeMillis()
        val edgarRss = "https://www.sec.gov/Archives/edgar/usgaap.rss.xml"
        val filingQueue = ArrayBlockingQueue<Filing>(200)
        val u = Uploader(filingQueue, 10)
        withHttpConnection(edgarRss, { http ->
            withInputStream(http, { inputStream ->
                withReader(inputStream, { reader ->
                    StreamReader(reader, filingQueue).processAll()
                    log.info("Finished reading filings.")
                })
            })
        })
        u.signalInputClosed(context)
        println(System.currentTimeMillis() - start)
    }

    fun withReader(input: InputStream, block: (XMLStreamReader) -> Unit) {
        block(XMLInputFactory.newInstance().createXMLStreamReader(input))
    }

    fun withInputStream(connection: HttpURLConnection, block: (InputStream) -> Unit) {
        var input: InputStream? = null
        try {
            input = connection.inputStream
            block(input)
        } finally {
            input?.close()
        }
    }

    fun withHttpConnection(url: kotlin.String, block: (HttpURLConnection) -> Unit) {
        var con: HttpURLConnection? = null
        try {
            con = URL(url).openConnection() as HttpURLConnection
            block(con)
        } finally {
            con?.disconnect()
        }
    }

}