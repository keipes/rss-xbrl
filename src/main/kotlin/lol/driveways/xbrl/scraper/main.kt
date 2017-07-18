package lol.driveways.xbrl.scraper

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.stream.XMLStreamReader


fun main(args: Array<String>) {
    val start = System.currentTimeMillis()
    withHttpConnection("https://www.sec.gov/Archives/edgar/usgaap.rss.xml", {withInputStream(it,
            {StreamProcessor(getReader(it)).processAll()})})
    println(System.currentTimeMillis() - start)
}

fun getReader(input: InputStream): XMLStreamReader {
    return javax.xml.stream.XMLInputFactory.newInstance().createXMLStreamReader(input)
}

fun <Anything> withInputStream(connection: HttpURLConnection, block: (InputStream) -> Anything) {
    var input: InputStream? = null
    try {
        input = connection.inputStream
        block(input)
    } finally {
        input?.close()
    }
}

fun <Anything> withHttpConnection(url: kotlin.String, block: (HttpURLConnection) -> Anything) {
    var con: HttpURLConnection? = null
    try {
        con = URL(url).openConnection() as HttpURLConnection
        block(con)
    } finally {
        con?.disconnect()
    }
}
