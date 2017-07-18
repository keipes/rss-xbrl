package lol.driveways.xbrl.scraper

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.stream.XMLStreamReader


fun main(args: Array<String>) {
    withInputStream("https://www.sec.gov/Archives/edgar/usgaap.rss.xml", {input ->
        val reader = getReader(input)
        val processor = StreamProcessor(reader)
        processor.processAll()
    })
}

fun getReader(input: InputStream): XMLStreamReader {
    return javax.xml.stream.XMLInputFactory.newInstance().createXMLStreamReader(input)
}


fun <Anything> withInputStream(url: kotlin.String, block: (InputStream) -> Anything): Anything {
    var con: HttpURLConnection? = null
    var input: InputStream? = null
    try {
        con = URL(url).openConnection() as HttpURLConnection
        input = con.inputStream
        return block(input)
    } finally {
        input?.close()
        con?.disconnect()
    }
}
