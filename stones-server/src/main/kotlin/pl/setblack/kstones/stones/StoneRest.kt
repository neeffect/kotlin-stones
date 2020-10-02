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
import pl.setblack.nee.ctx.web.WebContext
import pl.setblack.nee.effects.jdbc.JDBCProvider

class StoneRest(
    private val webContext: JDBCBasedWebContext,
    private val stoneService: StoneService,
    ) {



    fun api(): Routing.() -> Unit = {
        install(ContentNegotiation) {
            jackson {
                this.registerModule(VavrModule())
                this.registerModule(KotlinModule())
            }
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

    private fun <E, P, A> async(func: () -> Nee<Web, E, P, A>) =
        (Nee.constP(webContext.effects().async) { _ -> Unit } as Nee<Web, E, P, Unit>)
            .flatMap {
                println("in thread " + Thread.currentThread().name)
                func()
            }
}


