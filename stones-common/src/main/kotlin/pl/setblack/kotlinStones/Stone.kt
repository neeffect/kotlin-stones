package pl.setblack.kotlinStones


typealias  StoneId = Long

data class Stone(val id: StoneId, val data: StoneData)

data class StoneWithVotes(val stone:Stone, val votes: Int, val myVote: Boolean)

data class StoneData(val name: String, val color: String, val size: Int)
