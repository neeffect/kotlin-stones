package pl.setblack.kstones.stones

class StoneService(private val stoneRepo: StoneRepo) {

    fun allStones ()
            = stoneRepo.readAllStones()


}