package pl.setblack.kstones.web

import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
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
        routing(stonesModule.stoneRest.api())
    }
    server.start(wait = true)
}