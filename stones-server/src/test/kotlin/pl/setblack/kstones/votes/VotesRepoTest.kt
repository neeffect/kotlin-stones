package pl.setblack.kstones.votes

import dev.neeffect.nee.Nee
import dev.neeffect.nee.effects.test.get
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.vavr.control.Option
import io.vavr.kotlin.some
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kstones.db.DbSequence
import pl.setblack.kstones.dbModel.public_.Sequences
import pl.setblack.kstones.stones.StoneRepo
import pl.setblack.kstones.stones.TestCtx
import pl.setblack.kstones.stones.TestStonesDbSchema

class VotesRepoTest : DescribeSpec({
    describe("votes repo"){
        val wc = TestCtx.testCtx()
        val votesRepo = VotesRepo(TestCtx)
        val stonesRepo = StoneRepo(TestCtx, DbSequence(TestCtx, Sequences.GLOBALSTONESSEQ))

        it ("adds a vote") {
            val voter  = "testVoter"
            val testStone = StoneData("testStone","yellow", 10)
            TestStonesDbSchema.createDb().use {
                val addedVote = stonesRepo.addNewStone(testStone).flatMap { addedStone ->
                    addedStone.map { stoneId ->
                        votesRepo.voteStone(stoneId, voter)
                    }.getOrElse(Nee.pure(Option.none()))
                }
                addedVote.perform(wc)(Unit).get() shouldBe some(1L)
            }
        }

    }
})
