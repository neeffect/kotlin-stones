package pl.setblack.kstones.stones

import dev.neeffect.nee.Nee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import dev.neeffect.nee.effects.Out
import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.option
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kstones.infrastructure.InfrastuctureModule

class StoneService(
    private val ctx: JDBCBasedWebContextProvider,
    private val stoneRepo: StoneRepo
) {

    fun allStones() = Nee.withError(ctx.fx().nop) { context ->
        val z: Out<Nothing, Option<String>> = context.getSecurityContext().flatMap { it.getCurrentUser() }
            .map { user ->
                user.login
            }.handle({ _ ->
                Out.Companion.right<Nothing, Option<String>>(Option.none<String>())
            }, { login ->
                login.option()
            })
        z
    }.anyError().flatMap { votes: Option<String> ->
        stoneRepo.readAllStones(votes).anyError()
    }

    fun addStone(newStone: StoneData) = Nee.with(
        ctx.fx().secured(List.of(InfrastuctureModule.SecurityRoles.writer))
    ) {
    }.anyError().flatMap {
        stoneRepo.addNewStone(newStone).anyError()
    }

    fun getStone(id: StoneId) = stoneRepo.readStone(id)
}
