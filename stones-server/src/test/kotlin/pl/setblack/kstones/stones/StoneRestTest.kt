package pl.setblack.kstones.stones

import com.fasterxml.jackson.core.type.TypeReference
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.be
import io.kotest.matchers.should
import io.ktor.http.*
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.some
import pl.setblack.kotlinStones.Stone
import pl.setblack.kotlinStones.StoneId
import dev.neeffect.nee.effects.jdbc.JDBCProvider
import dev.neeffect.nee.security.test.TestDB
import dev.neeffect.nee.web.test.TestWebContextProvider

internal class StoneRestTest : DescribeSpec({
    describe("rest server") {
        val testWeb = object : TestWebContextProvider() {
        }
        TestDB(testWeb.jdbcConfig).initializeDb().use { testDb ->
            testDb.addUser("editor","editor",List.of("writer") )
            TestStonesDbSchema.updateDbSchema(testDb.connection)
            val testStonesModule = object : StonesModule() {
                override val jdbcProvider: JDBCProvider = JDBCProvider(testDb.connection)
            }
            val stoneRest = testStonesModule.stoneRest
            val engine = TestApplicationEngine(testWeb.testEnv)
            engine.start(false)
            engine.application.routing(stoneRest.api())
            it("should display no stones when empty") {

                val stones = engine.handleRequest(
                    HttpMethod.Get, "/stones"
                ).response.content
                stones!! should be("[]")
            }
            it ("should fail when adding stones without credentials") {
                val stonesResponseCode = engine.handleRequest(
                    HttpMethod.Post, "/stones"
                ) {
                    this.setBody("""{"name":"nikt", "color":"yellow", "size": 5}""")
                    this.addHeader("Content-Type", "application/json")
                    this.addHeader("Authorization", "Basic Wrong")
                }.response.status()


                stonesResponseCode should be(HttpStatusCode.Unauthorized)
            }
            it("should add  stone ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Post, "/stones"
                ) {
                    this.setBody("""{"name":"nikt", "color":"yellow", "size": 5}""")
                    this.addHeader("Content-Type", "application/json")
                    this.addHeader("Authorization", "Basic ZWRpdG9yOmVkaXRvcg==")
                }.response.content

                val stoneAdded = testWeb.jacksonMapper.readValue<Option<StoneId>>(stonesString, object : TypeReference<Option<StoneId>>() {
                })
                stoneAdded should be(some(1L))
            }
            it("should add  second stone ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Post, "/stones"
                ) {
                    this.setBody("""{"name":"burp", "color":"red", "size": 5}""")
                    this.addHeader("Content-Type", "application/json")
                    this.addHeader("Authorization", "Basic ZWRpdG9yOmVkaXRvcg==")
                }.response.content

                val stoneAdded = testWeb.jacksonMapper.readValue<Option<StoneId>>(stonesString, object : TypeReference<Option<StoneId>>() {
                })
                stoneAdded should be(some(2L))
            }
            it("added stones should be returned ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Get, "/stones"
                ) {
                    this.addHeader("Authorization", "Basic ZWRpdG9yOmVkaXRvcg==")
                }.response.content

                val stones = testWeb.jacksonMapper.readValue<List<Stone>>(stonesString,
                    object : TypeReference<List<Stone>>() {
                })
                stones.size() should be(2)
            }
            it("second stone should be returned ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Get, "/stones/2"
                ).response.content

                val stone = testWeb.jacksonMapper.readValue(stonesString, Stone::class.java)
                stone.data.name should be("burp")
            }
        }
    }
}) {

}




