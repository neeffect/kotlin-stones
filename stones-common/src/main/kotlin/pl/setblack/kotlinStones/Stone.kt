package pl.setblack.kotlinStones

import kotlinx.serialization.Serializable

typealias  StoneId = Long
@Serializable
data class Stone(val id: StoneId, val data: StoneData)

@Serializable
data class StoneWithVotes(val stone:Stone, val votes: Int, val myVote: Boolean) {
    fun upVoted() = this.copy(myVote = true, votes = votes+1)
}
@Serializable
data class StoneData(val name: String, val color: String, val size: Int)
