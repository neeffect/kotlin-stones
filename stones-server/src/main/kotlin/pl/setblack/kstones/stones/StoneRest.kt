package pl.setblack.kstones.stones

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
import pl.setblack.kstones.oauth.GoogleOauth

class StoneRest(
    private val webContext: JDBCBasedWebContextProvider,
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
            val id = call.parameters["id"]!!.toLong()
            val stone = stoneService.getStone()
            webContext.create(call).serveMessage(webContext.async { stone }, id)
        }
        get("/stones") {
            val stones = stoneService
                .allStones()
            webContext.create(call).serveMessage(webContext.async { stones }, Unit)
        }

        post("/stones") {
            val newStone = call.receive<StoneData>()

//            val authHeader = call.request.header("Authorization") ?: ""
//            println("dahaq $authHeader")
//            if (authHeader.startsWith("Bearer")) {
//                println(authHeader)
//                GoogleOauth.findUser(authHeader)
//            }
            println(newStone)
            val stoneAdded =
                stoneService.addStone(newStone)

            webContext.create(call).serveMessage(webContext.async { stoneAdded }, Unit)
        }
        get("/demo") {
            call.respondText("HELLO WORLD!")
        }
    }


}


