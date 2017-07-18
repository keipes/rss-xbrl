package lol.driveways.xbrl.scraper

import kotlin.reflect.full.memberProperties

class Filing {
    var accessionNumber: String? = null
    var formType: String? = null
    var filingDate: Number? = null
    var acceptanceDate: String? = null
    var cikNumber: Number? = null
    var enclosureUrl: String? = null
    var sic: Number? = null
    var companyName: String? = null


    override fun toString(): String {
        var s = "{"
        for (prop in Filing::class.memberProperties) {
            s += "\"${prop.name}\"=\"${prop.get(this)}\","
        }
        s.removeSuffix(",")
        return s + "}"
    }
}