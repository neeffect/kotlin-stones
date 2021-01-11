import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.list.*
import kotlinx.browser.window
import kotlinx.css.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import org.gciatto.kt.math.BigDecimal
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kotlinStones.StoneWithVotes
import react.functionalComponent
import react.useEffect
import react.useState
import styled.css
import styled.styledDiv
import kotlin.js.Promise

data class StonesState(
    val stones: List<StoneWithVotes> = listOf(),
    val newData: StoneData = StoneData("", "", 5)
)

val stonesList = functionalComponent<AppProps> { props ->
    val user = props.state.user
    val (stones, setStones) = useState(StonesState())

    useEffect(listOf(user)) {
        fetchStones(user).then {
            setStones(stones.copy(stones = it))
        }
    }


        mContainer {
            mCard {
                mCardHeader(title = "existing stones : ${user?.login}") {

                }
                mList {

                    for (stone in stones.stones) {
                        mListItem {
                            mListItemIcon {
                                mIcon("brightness_5")
                            }

                            styledDiv {
                                css {
                                    width = 100.px
                                }
                                styledDiv {
                                    css {
                                        val size = (stone.stone.data.size * 3).px
                                        borderRadius = 50.pct
                                        backgroundColor = Color(stone.stone.data.color)
                                        width = size
                                        height = size
                                    }
                                    +" "
                                }
                            }

                            mListItemText {
                                +stone.stone.data.name
                            }
                            mListItemSecondaryAction  {
                                +stone.votes.toString()

                                if (user!= null) {
                                    mIconButton("thumb_up", onClick = {
                                        if (!stone.myVote) {
                                            voteStone(stone.stone.id, user).then{
                                                val newStones: List<StoneWithVotes> = stones.stones.map { aStone: StoneWithVotes ->
                                                    if (aStone.stone.id == stone.stone.id) {
                                                        aStone.upVoted()

                                                    } else {
                                                        aStone
                                                    }
                                                }
                                                setStones(stones.copy(stones=newStones))
                                            }
                                        }
                                    }, color = if (stone.myVote) MColor.inherit else MColor.secondary, disabled = stone.myVote)
                                }
                            }
                        }
                    }
                }
            }
            if (user != null) {
                mCard(raised = false) {
                    mCardHeader(title = "add new stone") {

                    }
                    mCardContent {
                        mTextField(label = "Name", value = stones.newData.name, onChange = { event ->
                            setStones(stones.copy(newData = stones.newData.copy(name = event.targetInputValue)))
                        }) {
                        }

                        mTextField(label = "Color", value = stones.newData.color, onChange = { event ->
                            setStones(stones.copy(newData = stones.newData.copy(color = event.targetInputValue)))
                        }) {
                        }

                        mTextField(label = "Size", value = stones.newData.size.toString(), onChange = { event ->
                            setStones(stones.copy(newData = stones.newData.copy(size = event.targetInputValue.toInt())))
                        }) {
                        }
                    }


                    mCardActions {
                        mButton("add stone", MColor.primary, variant = MButtonVariant.contained, onClick = { _ ->
                            if (user != null) {
                                addStone(stones.newData, user)
                                    .then {
                                        fetchStones(user).then {
                                            setStones(stones.copy(stones = it))
                                        }
                                    }
                            }

                        }) {

                        }
                    }
                }
            }
        }

}


fun fetchStones(user: User?): Promise<List<StoneWithVotes>> =
    window.fetch("/api/stones", RequestInit(method = "GET",
        headers = Headers().apply {
            user?.let {
                set("Authorization", user.autHeader())
            }
        }))
        .then(Response::json)
        .then {
            Json.decodeFromDynamic<List<StoneWithVotes>>(it) }


fun addStone(newStone: StoneData, user: User): Promise<Long> =
    window.fetch(
        "/api/stones", RequestInit(method = "POST",
            headers = Headers().apply {
                set("Content-Type", "application/json")
                set("Authorization", user.autHeader())
            },
            body = JSON.stringify(newStone) { key, value ->
                when (value) {
                    is BigDecimal -> value.toString()
                    else -> value
                }
            })
    ).then { it.json().unsafeCast<Long>() }

fun voteStone(id:StoneId, user: User) : Promise<Unit> =
    window.fetch(
        "/api/stones/${id}/vote", RequestInit(method = "POST",
            headers = Headers().apply {
                set("Content-Type", "application/json")
                set("Authorization", user.autHeader())
            },
        )
    ).then { it.json().unsafeCast<Long>() }

