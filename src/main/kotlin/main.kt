import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.stream.XMLStreamReader


fun main(args: Array<String>) {
    withInputStream("https://www.sec.gov/Archives/edgar/usgaap.rss.xml", {input ->
        val reader = getReader(input)
        val processor = StreamProcessor(reader)
        processor.processAll()
//        readStream(reader)
    })
//    readStream(withInputStream("https://www.sec.gov/Archives/edgar/usgaap.rss.xml", {getReader(it)}))
//    withInputStream("https://www.sec.gov/Archives/edgar/usgaap.rss.xml", {
//        val reader = getReader(it).let {reader ->
//            while (reader.hasNext()) {
//                when (reader.next()) {
//                    XMLStreamReader.START_ELEMENT -> {
//                        val element = reader.localName
//                        println(element)
////                        val element = reader.getAttributeLocalName()
//                    }
//                }
//            }
//        }
//    })
}

fun readStream(reader: XMLStreamReader) {
    while (reader.hasNext()) {
        when (reader.next()) {
            XMLStreamReader.START_ELEMENT -> {
                val element = reader.localName
                println(element)
            }
        }
    }
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
