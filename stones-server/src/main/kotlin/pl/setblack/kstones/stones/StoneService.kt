package pl.setblack.kstones.stones

import pl.setblack.nee.Nee
import pl.setblack.nee.ctx.web.WebContext
import io.vavr.collection.List

class StoneService(private val stoneRepo: StoneRepo) {

    fun allStones() = stoneRepo.readAllStones()

    fun addStone(newStone: StoneData) = Nee.constP(
        WebContext.Effects.secured(List.of(StonesModule.SecurityRoles.writer))
    ) {
    }.anyError().flatMap {
        stoneRepo.addNewStone(newStone)
    }
}