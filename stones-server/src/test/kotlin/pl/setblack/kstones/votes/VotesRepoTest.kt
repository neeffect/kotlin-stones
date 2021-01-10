package pl.setblack.kstones.votes

import dev.neeffect.nee.Nee
import dev.neeffect.nee.effects.test.get
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.fp.some


import io.kotest.matchers.shouldBe
import io.vavr.control.Option
import io.vavr.kotlin.none
import io.vavr.kotlin.option

import io.vavr.kotlin.some
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kstones.db.DbSequence
import pl.setblack.kstones.dbModel.public_.Sequences
import pl.setblack.kstones.stones.StoneRepo
import pl.setblack.kstones.stones.TestCtx
import pl.setblack.kstones.stones.TestStonesDbSchema

class VotesRepoTest : DescribeSpec({
    describe("votes repo") {
        val wc = TestCtx.testCtx()
        val votesRepo = VoteRepo(TestCtx)
        val stonesRepo = StoneRepo(TestCtx, DbSequence(TestCtx, Sequences.GLOBALSTONESSEQ))
        val voter  = "testVoter"
        val testStone = StoneData("testStone", "yellow", 10)
        it("adds a vote") {
            TestStonesDbSchema.createDb().use {
                val addedVote = stonesRepo.addNewStone(testStone).flatMap { addedStone ->
                    addedStone.map { stoneId ->
                        votesRepo.voteStone(stoneId, voter)
                    }.getOrElse(Nee.pure(Option.none()))
                }
                addedVote.perform(wc).get() shouldBe some(VoteId(1L))
            }
        }
        it ("adds a vote only once for a stone") {
            TestStonesDbSchema.createDb().use {
                val stoneAdded = stonesRepo.addNewStone(testStone)
                val addedVote1 = votesRepo.voteStone(1L, voter)
                val addedVote2 = votesRepo.voteStone(1L, voter)

                stoneAdded.perform(wc).flatMap {
                    addedVote1.perform(wc).flatMap {
                        addedVote2.perform(wc)
                    }
                }.get() shouldBe Option.none<VoteId>()
            }
        }
        it ("adds votes  for 2 various stones") {
            TestStonesDbSchema.createDb().use {
                val stoneAdded1 = stonesRepo.addNewStone(testStone)
                val stoneAdded2 = stonesRepo.addNewStone(testStone)
                val addedVote1 = votesRepo.voteStone(1L, voter)
                val addedVote2 = votesRepo.voteStone(2L, voter)
                stoneAdded1.perform(wc).flatMap {
                    stoneAdded2.perform(wc).flatMap {
                        addedVote1.perform(wc).flatMap {
                            addedVote2.perform(wc)
                        }
                    }
                }.get() shouldBe some(VoteId(2L))
            }
        }
        it ("calulates votes  for 2 various votes") {
            TestStonesDbSchema.createDb().use {
                val stoneAdded = stonesRepo.addNewStone(testStone)
                val addedVote1 = votesRepo.voteStone(1L, voter)
                val addedVote2 = votesRepo.voteStone(1L, "voter2")

                stoneAdded.perform(wc).flatMap {
                    addedVote1.perform(wc).flatMap {
                        addedVote2.perform(wc).flatMap {
                            votesRepo.calcVotes(1L).perform(wc)
                        }
                    }
                }.get() shouldBe 2L
            }
        }
        it ("should see votes in StonesRepo") {
            TestStonesDbSchema.createDb().use {
                val stoneAdded = stonesRepo.addNewStone(testStone)
                val addedVote1 = votesRepo.voteStone(1L, voter)
                val addedVote2 = votesRepo.voteStone(1L, "voter2")

                stoneAdded.perform(wc).flatMap {
                    addedVote1.perform(wc).flatMap {
                        addedVote2.perform(wc).flatMap {
                            stonesRepo.readAllStones().perform(wc)
                        }
                    }
                }.get().get(0).votes shouldBe 2
            }
        }

            
            listOf (
                "testVoter".option() to true,
                "otherVoter".option() to false,
                none<VoterId>() to false).
                forEach { (whoAsks, result) ->
                    it("should see voter $whoAsks in StonesRepo response"){
                        TestStonesDbSchema.createDb().use {
                            val stoneAdded = stonesRepo.addNewStone(testStone)
                            val addedVote1 = votesRepo.voteStone(1L, voter)
                            val addedVote2 = votesRepo.voteStone(1L, "voter2")

                            stoneAdded.perform(wc).flatMap {
                                addedVote1.perform(wc).flatMap {
                                    addedVote2.perform(wc).flatMap {
                                        stonesRepo.readAllStones(whoAsks).perform(wc)
                                    }
                                }
                            }.get().get(0).myVote shouldBe result
                        }
                }

        }
    }
})
