package pl.setblack.kstones.stones

import com.fasterxml.jackson.core.type.TypeReference
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.be
import io.kotest.matchers.should
import io.ktor.http.HttpMethod
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.some
import pl.setblack.nee.security.test.TestDB
import pl.setblack.nee.web.test.TestWebContext

internal class StoneRestTest : DescribeSpec({
    describe("rest server") {

        val testWeb = object : TestWebContext() {
        }
        TestDB(testWeb.jdbcConfig).initializeDb().use { testDb ->
            testDb.addUser("editor","editor",List.of("writer") )
            TestStonesDbSchema.updateDbSchema(testDb.connection)
            val testStonesModule = object : StonesModule() {
                override val jdbcConfig = testDb.jdbcConfig
            }
            val stoneService = testStonesModule.stoneRest
            val engine = TestApplicationEngine(testWeb.testEnv)
            engine.start(false)
            engine.application.routing(stoneService.api())
            it("should display no stones when empty") {

                val stones = engine.handleRequest(
                    HttpMethod.Get, "/stones"
                ).response.content
                stones!! should be("[]")
            }
            it("should add  stone ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Post, "/stones"
                ) {
                    this.setBody("""{"name":"nikt", "price":27.80}""")
                    this.addHeader("Content-Type", "application/json")
                    this.addHeader("Authorization", "Basic ZWRpdG9yOmVkaXRvcg==")
                }.response.content

                val stoneAdded = testStonesModule.objectMapper.readValue<Option<StoneId>>(stonesString, object : TypeReference<Option<StoneId>>() {
                })
                stoneAdded should be(some(1L))
            }
            it("should add  second stone ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Post, "/stones"
                ) {
                    this.setBody("""{"name":"burp", "price":17.20}""")
                    this.addHeader("Content-Type", "application/json")
                    this.addHeader("Authorization", "Basic ZWRpdG9yOmVkaXRvcg==")
                }.response.content

                val stoneAdded = testStonesModule.objectMapper.readValue<Option<StoneId>>(stonesString, object : TypeReference<Option<StoneId>>() {
                })
                stoneAdded should be(some(2L))
            }
            it("added stones should be returned ") {

                val stonesString = engine.handleRequest(
                    HttpMethod.Get, "/stones"
                ) {
                    this.addHeader("Authorization", "Basic ZWRpdG9yOmVkaXRvcg==")
                }.response.content

                val stones = testStonesModule.objectMapper.readValue<List<StoneId>>(stonesString, object : TypeReference<List<Stone>>() {
                })
                stones.size() should be(2)
            }
        }
    }
}) {

}




