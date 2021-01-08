package pl.setblack.kstones.votes

import dev.neeffect.nee.Nee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import io.vavr.control.Option
import org.jooq.impl.DSL
import org.jooq.impl.DSL.count
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kstones.db.DbSequence
import pl.setblack.kstones.db.SequenceGenerator
import pl.setblack.kstones.dbModel.public_.Sequences
import pl.setblack.kstones.dbModel.public_.tables.Votes
import pl.setblack.kstones.stones.Web


typealias VoterId = String

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class VoteId(val id: Long)

class VoteRepo(
    private val ctx: JDBCBasedWebContextProvider
) {

    private val seq: SequenceGenerator<Web> = DbSequence(ctx, Sequences.GLOBALVOTESSEQ)

    fun voteStone(stone: StoneId, voter: VoterId) =
        existsVote(stone,voter).flatMap { exists ->
            if (!exists) {
                seq.next().flatMap { voteId ->
                    addVote(VoteId(voteId), stone, voter)
                }
            } else {
                Nee.pure(Option.none())
            }
        }

    fun calcVotes(stone: StoneId) = Nee.with(ctx.fx().tx) {jdbcProvider ->
        DSL.using(jdbcProvider.getConnection().getResource()).let {dsl->
            dsl.select(count()).from(Votes.VOTES).where(
                Votes.VOTES.STONE_ID.eq(stone)
            ).fetchSingle(0) as Int
        }
    }

    private fun addVote(id: VoteId, stone: StoneId, voter: VoterId) = Nee.with(ctx.fx().tx) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        val insertedRows = dsl.insertInto(Votes.VOTES)
            .values(id.id, stone, voter)
            .execute()
        if (insertedRows == 1) {
            Option.some(id)
        } else {
            Option.none()
        }
    }

    private fun existsVote(stone:StoneId, voter: VoterId) = Nee.with(ctx.fx().tx) {jdbcProvider ->
        DSL.using(jdbcProvider.getConnection().getResource()).let { dsl ->
            val counted = dsl.select(count()).from(Votes.VOTES)
                .where( Votes.VOTES.STONE_ID.eq(stone).and(Votes.VOTES.VOTER.eq(voter) ))
                .fetchSingle(0) as Int
            counted > 0
        }

    }
}
