package pl.setblack.kstones.stones

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import dev.neeffect.nee.effects.security.SecurityErrorType
import dev.neeffect.nee.effects.test.get
import dev.neeffect.nee.security.test.TestDB
import dev.neeffect.nee.web.test.TestWebContextProvider
import io.vavr.collection.List
import io.vavr.kotlin.some
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kstones.infrastructure.InfrastuctureModule
import pl.setblack.kstones.stones.StoneRestTest.Companion.OauthTestConfig.jwtModule

internal class StoneServiceTest : DescribeSpec({
    describe("stone service") {
        val testWeb = TestWebContextProvider()
        val wc = testWeb.testCtx()
        val stoneService = StonesModule(InfrastuctureModule(jwtModule)).stoneService

        it("should have no stones in init db") {
            TestDB(testWeb.jdbcConfig).initializeDb().use {
                TestStonesDbSchema.updateDbSchema(it.connection). use {
                    val result = stoneService.allStones().perform(wc)
                    result.get().toJavaList().shouldBeEmpty()
                }
            }
        }
        describe("anonymous user") {
            it("should not be able to add stone") {
                TestDB(testWeb.jdbcConfig).initializeDb().use {

                    val result = stoneService.addStone(testStone).perform(wc)
                    result.toFuture().get().swap()
                        .get() shouldBe SecurityErrorType.MissingRole(List.of(InfrastuctureModule.SecurityRoles.writer))
                }
            }
        }
        describe("writer user") {
            val editorCall = TestWebContextProvider().testCtx { req ->
                req.addHeader("Authorization", "Basic ZWRpdG9yOmVkaXRvcg==")
            }
            it("should be able to add stone") {
                TestDB(testWeb.jdbcConfig).initializeDb().use { testDb ->
                    TestStonesDbSchema.updateDbSchema(testDb.connection). use {
                        testDb.addUser("editor", "editor", List.of(InfrastuctureModule.SecurityRoles.writer.roleName))

                        val result =
                            stoneService.addStone(testStone).perform(editorCall)
                        result.get() shouldBe (some(1L))
                    }
                }
            }
            it("should be able to add and read stone") {
                TestDB(testWeb.jdbcConfig).initializeDb().use { testDb ->
                    TestStonesDbSchema.updateDbSchema(testDb.connection). use {
                        testDb.addUser("editor", "editor", List.of(InfrastuctureModule.SecurityRoles.writer.roleName))

                        val added =
                            stoneService.addStone(testStone).perform(editorCall)
                        val addedStoneId = added.toFuture().get().get().get()
                        val result = stoneService.getStone(addedStoneId).perform(editorCall)

                        result.get().data.name shouldBe ("old1")
                    }
                }
            }
        }
    }

}) {
    companion object {
        val testStone = StoneData("old1", "gray", 5)
    }
}
