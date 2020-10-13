package pl.setblack.kotlinStones


typealias  StoneId = Long

data class Stone(val id: StoneId, val data: StoneData)

data class StoneData(val name: String, val color: String, val size: Int)
