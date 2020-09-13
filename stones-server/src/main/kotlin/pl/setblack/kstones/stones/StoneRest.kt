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
    private val stoneService: StoneService,
    private val aJdbcProvider1: JDBCProvider) {

    val webContext = object  : JDBCBasedWebContext(){
        override val jdbcProvider = aJdbcProvider1
    }
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
}

fun <E, P, A> async(func: () -> Nee<WebContext, E, P, A>) =
    (Nee.constP(WebContext.Effects.async) { _ -> Unit } as Nee<WebContext, E, P, Unit>)
        .flatMap {
            println("in thread " + Thread.currentThread().name)
            func()
        }
