package pl.setblack.kstones.stones

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.vavr.jackson.datatype.VavrModule
import pl.setblack.nee.Nee
import pl.setblack.nee.ctx.web.JDBCBasedWebContext
import pl.setblack.nee.effects.monitoring.CodeNameFinder

class StoneRest(
    private val webContext: JDBCBasedWebContext,
    private val stoneService: StoneService,
    ) {



    fun api(): Route.() -> Unit = {
        install(ContentNegotiation) {
            jackson {
                this.registerModule(VavrModule())
                this.registerModule(KotlinModule())
            }
        }
        get("/stones/{id}") {
            val id =  call.parameters["id"]!!.toLong()
            val stone = stoneService.getStone()
            webContext.create(call).serveMessage(async { stone }, id)
        }
        get("/stones") {
            val stones = stoneService
                .allStones()
            webContext.create(call).serveMessage(async { stones }, Unit)
        }

        post("/stones"){
            val newStone = call.receive<StoneData>()
            println(newStone)
            val stoneAdded =
                    stoneService.addStone(newStone)

            webContext.create(call).serveMessage(async { stoneAdded }, Unit)
        }
        get("/demo") {
            call.respondText("HELLO WORLD!")
        }
    }

    private fun <E, P, A> async(func: () -> Nee<Web, E, P, A>) : Nee<Web, Any, P , A> =
            CodeNameFinder.guessCodePlaceName(2).let { whereItIsDefined ->
                Nee.pure(webContext.effects().async) { r: Web ->
                    { p: P ->
                        r.getTrace().putNamedPlace(whereItIsDefined)
                        func()
                    }
                }
                    .flatMap { it.anyError() }
            }



}


