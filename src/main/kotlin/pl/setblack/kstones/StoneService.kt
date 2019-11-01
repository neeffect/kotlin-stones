package pl.setblack.kstones

import io.vavr.collection.List

class StoneService(private val stoneRepo: StoneRepo) {

    fun allStones ()
            = stoneRepo.readAllStones()


}