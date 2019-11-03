package pl.setblack.kstones.stones

import pl.setblack.kstones.stones.StoneRepo

class StoneService(private val stoneRepo: StoneRepo) {

    fun allStones ()
            = stoneRepo.readAllStones()


}