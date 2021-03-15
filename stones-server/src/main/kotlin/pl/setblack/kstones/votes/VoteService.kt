package pl.setblack.kstones.votes

import dev.neeffect.nee.Nee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import io.vavr.collection.List
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kstones.infrastructure.InfrastuctureModule

class VoteService(
    private val context: JDBCBasedWebContextProvider,
    private val voteRepo: VoteRepo
) {

    fun vote(stoneId: StoneId) = Nee.withError(
        context.fx().secured(List.of(InfrastuctureModule.SecurityRoles.writer))
    ) { ctx ->
        ctx.getSecurityContext().flatMap { security ->
            security.getCurrentUser()
        }.map { user ->
            user.login
        }
    }.anyError().flatMap { user ->
        voteRepo.voteStone(stoneId, user).anyError()
    }
}
