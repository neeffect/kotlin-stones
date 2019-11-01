package pl.setblack.kstones.web

import io.ktor.features.ContentNegotiation
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import pl.setblack.kstones.StoneRepo
import pl.setblack.kstones.StoneService
import pl.setblack.kstones.db.DbSequence
import io.ktor.jackson.jackson
import io.ktor.application.install
import io.vavr.jackson.datatype.VavrModule

fun main() {
    val seq = DbSequence()
    val stoneRepo = StoneRepo(seq)
    val stoneService = StoneService(stoneRepo)
    val stoneRest = StoneRest(stoneService)

    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            jackson {
                this.registerModule(VavrModule())
            }
        }

        routing(stoneRest.api())
    }
    server.start(wait = true)
}