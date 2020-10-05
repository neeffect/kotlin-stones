package pl.setblack.kstones.web

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.vavr.jackson.datatype.VavrModule
import pl.setblack.kstones.stones.StonesModule

fun main() {
    val stonesModule = StonesModule()

    val server = embeddedServer(Netty, port = 3000) {
        install(ContentNegotiation) {
            jackson {
                this.registerModule(VavrModule())
            }
        }
        install(StatusPages) {
            exception<Throwable> { cause ->
                cause.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        routing(stonesModule.stoneRest.api())
        routing(stonesModule.context.sysApi())
    }
    server.start(wait = true)
}


