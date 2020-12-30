package pl.setblack.kstones.votes

import dev.neeffect.nee.Nee
import dev.neeffect.nee.UANee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import io.vavr.control.Option
import org.jooq.impl.DSL
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kstones.db.DbSequence
import pl.setblack.kstones.db.SequenceGenerator
import pl.setblack.kstones.dbModel.public_.Sequences
import pl.setblack.kstones.dbModel.public_.tables.Stones
import pl.setblack.kstones.dbModel.public_.tables.Votes
import pl.setblack.kstones.stones.Web


typealias VoterId = String


typealias  VoteId = Long

class VotesRepo(
    private  val ctx: JDBCBasedWebContextProvider
) {

    private val seq: SequenceGenerator<Web> = DbSequence(ctx,  Sequences.GLOBALVOTESSEQ)

    fun voteStone(stone: StoneId, voter : VoterId): UANee<Web, Option<VoteId>> = seq.next().flatMap {voteId ->
        addVote(voteId, stone, voter)
    }

    fun calcVotes(stone:StoneId) : Int = TODO("$stone")

    private fun addVote(id:VoteId, stone: StoneId, voter: VoterId) = Nee.constP(ctx.fx().tx) { jdbcProvider ->
        val dsl = DSL.using(jdbcProvider.getConnection().getResource())
        val insertedRows = dsl.insertInto(Votes.VOTES)
            .values(id, stone, voter)
            .execute()
        if (insertedRows == 1) {
            Option.some(id)
        } else {
            Option.none()
        }
    }
}
