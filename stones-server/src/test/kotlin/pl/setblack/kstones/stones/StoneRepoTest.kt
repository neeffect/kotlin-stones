package pl.setblack.kstones.stones

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.vavr.control.Option
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kstones.db.DbSequence
import dev.neeffect.nee.Nee
import dev.neeffect.nee.web.test.TestWebContextProvider
import pl.setblack.kstones.dbModel.public_.Sequences

internal class StoneRepoTest : BehaviorSpec({
    Given("a repo") {
        val wc = TestCtx.testCtx()
        val repo = StoneRepo(TestCtx, DbSequence(TestCtx, Sequences.GLOBALSTONESSEQ))
        When(" stone inserted into db") {
            val stone = StoneData("old1", "gray", 5)
            val insertedStoneId = repo.addNewStone(stone)
            Then("stone can be read") {
                TestStonesDbSchema.createDb().use {
                    val result = insertedStoneId.flatMap { maybeStoneId ->
                        maybeStoneId.map { stoneId ->
                            repo.readStone(stoneId).map {
                                Option.some(it)
                            }
                        }.getOrElse(Nee.pure(Option.none()))
                    }.perform(wc).toFuture().get()
                    result.get().get().data.name shouldBe "old1"
                }
            }

            Then("stone will be in  all stones") {
                TestStonesDbSchema.createDb().use {
                    val result = insertedStoneId.flatMap {
                        repo.readAllStones()
                    }.perform(wc).toFuture().get()
                    result.get().size() shouldBe 1
                }
            }
        }
    }

}) {



}

object TestCtx:TestWebContextProvider()
