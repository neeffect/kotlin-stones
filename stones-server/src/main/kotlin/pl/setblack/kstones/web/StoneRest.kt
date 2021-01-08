package pl.setblack.kstones.web

import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.vavr.jackson.datatype.VavrModule
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kstones.stones.StoneService
import pl.setblack.kstones.votes.VoteService

class StoneRest(
    private val webContext: JDBCBasedWebContextProvider,
    private val stoneService: StoneService,
    private val voteService: VoteService
) {

    fun api(): Route.() -> Unit = {
        install(ContentNegotiation) {
            jackson {
                this.registerModule(VavrModule())
                this.registerModule(KotlinModule())
            }
        }
        get("/stones/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val stone = stoneService.getStone(id)
            webContext.create(call).serveMessage(webContext.async { stone })
        }
        get("/stones") {
            val stones = stoneService
                .allStones()
            webContext.create(call).serveMessage(webContext.async { stones })
        }
        post("/stones/{id}/vote") {
            val id = call.parameters["id"]!!.toLong()
            val res = voteService.vote(id)
            webContext.create(call).serveMessage(webContext.async { res })
        }

        post("/stones") {
            val newStone = call.receive<StoneData>()
            val stoneAdded =
                stoneService.addStone(newStone)
            webContext.create(call).serveMessage(webContext.async { stoneAdded })
        }
        get("/demo") {
            call.respondText("HELLO WORLD!")
        }
    }


}


