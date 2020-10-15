package pl.setblack.kstones.stones

import dev.neeffect.nee.Nee
import dev.neeffect.nee.ctx.web.JDBCBasedWebContextProvider
import io.vavr.collection.List
import pl.setblack.kotlinStones.StoneData

class StoneService(
    private  val context: JDBCBasedWebContextProvider,
    private val stoneRepo: StoneRepo) {

    fun allStones() = stoneRepo.readAllStones()

    fun addStone(newStone: StoneData) = Nee.constP(
        context.fx().secured(List.of(StonesModule.SecurityRoles.writer))
    ) {
    }.anyError().flatMap {
        stoneRepo.addNewStone(newStone)
    }

    fun getStone() = stoneRepo.readStone()
}
