package pl.setblack.kstones.stones

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.vavr.control.Option
import pl.setblack.kstones.db.DbSequence
import pl.setblack.nee.Nee
import pl.setblack.nee.web.test.TestWebContext

internal class StoneRepoTest : BehaviorSpec({
    Given("a repo") {
        val repo = StoneRepo(DbSequence())
        val wc = TestCtx.testCtx()
        When(" stone inserted into db") {
            val stone = StoneData("old1", 4.toBigDecimal())
            val insertedStoneId = repo.addNewStone(stone)
            Then("stone can be read") {
                TestStonesDbSchema.createDb().use {
                    val result = insertedStoneId.flatMap { maybeStoneId ->
                        maybeStoneId.map { stoneId ->
                            println("reading stone")
                            repo.readStone().constP()(stoneId).map {
                                Option.some(it)
                            }
                        }.getOrElse(Nee.pure(Option.none()))
                    }.perform(wc)(Unit).toFuture().get()
                    println("performed")
                    result.get().get().data.name shouldBe "old1"
                }
            }

            Then("stone will be in  all stones") {
                TestStonesDbSchema.createDb().use {
                    val result = insertedStoneId.flatMap {
                        repo.readAllStones()
                    }.perform(wc)(Unit).toFuture().get()
                    result.get().size() shouldBe 1
                }
            }
        }
    }

}) {



}

object TestCtx:TestWebContext()