package pl.setblack.kstones.web

import io.ktor.features.ContentNegotiation
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import pl.setblack.kstones.stones.StoneRepo
import pl.setblack.kstones.stones.StoneService
import pl.setblack.kstones.db.DbSequence
import io.ktor.jackson.jackson
import io.ktor.application.install
import io.vavr.jackson.datatype.VavrModule
import pl.setblack.kstones.stones.StonesModule

fun main() {
    val stonesModule = StonesModule()

    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            jackson {
                this.registerModule(VavrModule())
            }
        }
        routing(stonesModule.stoneRest.get().api())
    }
    server.start(wait = true)
}