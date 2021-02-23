@file:UseSerializers(VavrSerializers.OptionSerializer::class, VavrSerializers.ListSerializer::class)
package pl.setblack.kstones.web

import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import dev.neeffect.nee.serializers.UUIDSerializer
import dev.neeffect.nee.serializers.VavrSerializers
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.serialization.json
import io.vavr.control.Option
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
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
            json  ( Json {
                     serializersModule = module
            })
        }
        get("/stones/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val stone = stoneService.getStone(id)
            webContext.create(call).serveMessage(webContext.async { stone })
        }
        get("/stones") {
            val stones = stoneService
                .allStones().map {  list -> list.toJavaList() }
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
            webContext.create(call).serveMessage<Option<Long>>(webContext.async { stoneAdded })
        }
        get("/demo") {
            call.respondText("HELLO WORLD!")
        }
    }


}


@Suppress("UNCHECKED_CAST")
val module = SerializersModule {
    //val anyOptionSerializer = serializer(Option::class.java) as KSerializer<Option<*>>
    val zSerializer =  serializer<Option<Any>>()
    contextual(UUIDSerializer())
    polymorphic(Option::class) {
        default { _ ->
//            anyOptionSerializer
            zSerializer
        }
    }
}
