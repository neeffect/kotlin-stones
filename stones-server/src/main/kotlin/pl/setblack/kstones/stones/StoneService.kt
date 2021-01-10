package pl.setblack.kstones.stones

import dev.neeffect.nee.Nee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.none
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kstones.infrastructure.InfrastuctureModule

class StoneService(
    private  val context: JDBCBasedWebContextProvider,
    private val stoneRepo: StoneRepo) {

    fun allStones(votesOf: Option<String> = none()) = stoneRepo.readAllStones(votesOf)

    fun addStone(newStone: StoneData) = Nee.with(
        context.fx().secured(List.of(InfrastuctureModule.SecurityRoles.writer))
    ) {
    }.anyError().flatMap {
        stoneRepo.addNewStone(newStone).anyError()
    }

    fun getStone(id: StoneId) = stoneRepo.readStone(id)
}
