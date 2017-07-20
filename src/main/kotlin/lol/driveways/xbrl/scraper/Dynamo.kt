package lol.driveways.xbrl.scraper

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.TableWriteItems
import java.util.logging.Logger

class Dynamo {

    companion object {
        val log: Logger = Logger.getLogger(Dynamo::class.java.simpleName)
        val filingTable = "filings"
        val keyAccessionNumber = "accession_number"
        val keyPubDate = "pub_date"
        val keyCik = "cik"
        val keyForm = "form"
        val maxRetries = 5
    }

    val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build()

    val dynamoDB = DynamoDB(client)

    fun writeFilings(filings: List<Filing>) {
        log.info("Writing ${filings.size} items to DynamoDB")
        var retry: Double = 0.0
        var result = dynamoDB.batchWriteItem(filingsTableWrite(filings.map {filingToItem(it)}))
        while (!result.unprocessedItems.isEmpty() && retry < maxRetries) {
            val backoff = Math.pow(2.0, retry) * 500
            log.info("Will retry ${result.unprocessedItems.size} items after ${backoff}ms")
            result = dynamoDB.batchWriteItemUnprocessed(result.unprocessedItems)
            retry += 1
        }
        if (!result.unprocessedItems.isEmpty()) {
            log.warning("Gave up on uploading ${result.unprocessedItems.size} items")
        }
    }

    fun filingsTableWrite(items: List<Item>): TableWriteItems {
        return TableWriteItems(filingTable)
            .withItemsToPut(items)
    }

    fun filingToItem(filing: Filing): Item {
        return Item().withPrimaryKey(keyAccessionNumber, filing.accessionNumber)
                .withNumber(keyPubDate, filing.filingDate)
                .withNumber(keyCik, filing.cikNumber)
                .withString(keyForm, filing.formType)
    }

}
