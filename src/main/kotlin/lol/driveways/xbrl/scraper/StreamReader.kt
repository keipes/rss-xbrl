package lol.driveways.xbrl.scraper

import java.text.SimpleDateFormat
import java.util.concurrent.BlockingQueue
import javax.xml.stream.XMLStreamReader

class StreamReader (val reader: XMLStreamReader, val filingQueue: BlockingQueue<Filing>) {

    private val tagItem = "item"
    private val tagAccessionNumber = "edgar:accessionNumber"
    private val tagFormType = "edgar:formType"
    private val tagFilingDate = "pubDate"
    private val tagAcceptanceDate = "edgar:acceptanceDatetime"
    private val tagCikNumber = "edgar:cikNumber"
    private val tagEnclosure = "enclosure"
    private val tagSic = "edgar:assignedSic"
    private val tagCompanyName = "edgar:companyName"

    private val feedTimeFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")

    private var curFiling: Filing = Filing()

    fun processAll() {
        while (reader.hasNext()) {
            when (reader.next()) {
                XMLStreamReader.START_ELEMENT -> startElement()
                XMLStreamReader.END_ELEMENT -> endElement()
            }
        }
    }

    private fun prettyName(): String {
        val prefix = reader.prefix
        if (prefix != "") {
            return "$prefix:${reader.localName}"
        } else {
            return reader.localName
        }
    }

    private fun startElement() {
        when (prettyName()) {
            tagItem -> {curFiling = Filing() }
            tagAccessionNumber -> {curFiling.accessionNumber = reader.elementText}
            tagFormType -> {curFiling.formType = reader.elementText}
            tagFilingDate -> {curFiling.filingDate = feedTimeFormat.parse(reader.elementText).time}
            tagAcceptanceDate -> {curFiling.acceptanceDate = reader.elementText}
            tagCikNumber -> {curFiling.cikNumber = Integer.parseInt(reader.elementText)}
            tagEnclosure -> {curFiling.enclosureUrl = reader.getAttributeValue(null, "url")}
            tagSic -> {curFiling.sic = Integer.parseInt(reader.elementText)}
            tagCompanyName -> {curFiling.companyName = reader.elementText}
        }
    }
    private fun endElement() {
        when (prettyName()) {
            tagItem -> {filingQueue.put(curFiling)
            }
        }
    }

}