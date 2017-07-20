package lol.driveways.xbrl.scraper

import java.util.logging.Logger

val log: Logger = Logger.getLogger("main")

fun main(args: Array<String>) {
    val lh = LambdaHandler()
    lh.handleRequest("", null)
}
