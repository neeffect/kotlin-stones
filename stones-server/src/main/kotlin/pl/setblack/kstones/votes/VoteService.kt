package pl.setblack.kstones.votes

import dev.neeffect.nee.Nee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import io.vavr.collection.List
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kstones.stones.StonesModule

class VoteService(
    private  val context: JDBCBasedWebContextProvider,
    private val votesRepo: VotesRepo) {

    fun vote(stoneId: StoneId) = Nee.withError(
        context.fx().secured(List.of(StonesModule.SecurityRoles.writer))
    ) { ctx ->
        ctx.getSecurityContext().flatMap{ security ->
            security.getCurrentUser()
        }.map { user ->
            user.login
        }
    }.anyError().flatMap { user ->
        votesRepo.voteStone(stoneId, user).anyError()
    }
}
