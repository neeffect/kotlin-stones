package pl.setblack.kstones.stones

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import pl.setblack.nee.effects.security.SecurityErrorType
import pl.setblack.nee.security.test.TestDB
import pl.setblack.nee.web.test.TestWebContext
import io.vavr.collection.List
import io.vavr.kotlin.some

internal class StoneServiceTest : DescribeSpec({
    describe("stone service") {
        val testWeb = TestWebContext()
        val wc = testWeb.testCtx()
        val stoneService = StonesModule().stoneService

        it("should have no stones in init db") {
            TestDB(testWeb.jdbcConfig).initializeDb().use {
                TestStonesDbSchema.updateDbSchema(it.connection). use {
                    val result = stoneService.allStones().perform(wc)(Unit)
                    result.toFuture().get().get().toJavaList().shouldBeEmpty()
                }
            }
        }
        describe("anonymous user") {
            it("should not be able to add stone") {
                TestDB(testWeb.jdbcConfig).initializeDb().use {

                    val result = stoneService.addStone(testStone).perform(wc)(Unit)
                    result.toFuture().get().swap()
                        .get() shouldBe SecurityErrorType.MissingRole(List.of(StonesModule.SecurityRoles.writer))
                }
            }
        }
        describe("writer user") {
            val editorCall = TestWebContext().testCtx { req ->
                req.addHeader("Authorization", "Basic ZWRpdG9yOmVkaXRvcg==")
            }
            it("should be able to add stone") {
                TestDB(testWeb.jdbcConfig).initializeDb().use { testDb ->
                    TestStonesDbSchema.updateDbSchema(testDb.connection). use {
                        testDb.addUser("editor", "editor", List.of(StonesModule.SecurityRoles.writer.roleName))

                        val result =
                            stoneService.addStone(testStone).perform(editorCall)(Unit)
                        result.toFuture().get().get() shouldBe (some(1L))
                    }
                }
            }
        }
    }

}) {
    companion object {
        val testStone = StoneData("old1", 4.toBigDecimal())
    }
}
