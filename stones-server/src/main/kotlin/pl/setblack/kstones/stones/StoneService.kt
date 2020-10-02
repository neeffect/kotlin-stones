package pl.setblack.kstones.stones

import pl.setblack.nee.Nee
import pl.setblack.nee.ctx.web.WebContext
import io.vavr.collection.List
import pl.setblack.nee.ctx.web.JDBCBasedWebContext

class StoneService(
    private  val context: JDBCBasedWebContext,
    private val stoneRepo: StoneRepo) {

    fun allStones() = stoneRepo.readAllStones()

    fun addStone(newStone: StoneData) = Nee.constP(
        context.effects().secured(List.of(StonesModule.SecurityRoles.writer))
    ) {
    }.anyError().flatMap {
        stoneRepo.addNewStone(newStone)
    }
}
