import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemIcon
import kotlinx.browser.window
import org.gciatto.kt.math.BigDecimal
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import pl.setblack.kotlinStones.Stone
import pl.setblack.kotlinStones.StoneData
import react.dom.div
import react.functionalComponent
import react.useEffect
import react.useState
import kotlin.js.Promise

data class StonesState(val stones: List<Stone> = listOf(), val newName: String = "")


val stonesList = functionalComponent<AppProps> { props ->
    val user = props.state.user
    val (stones, setStones) = useState(StonesState())

    useEffect(emptyList()) {
        fetchStones().then {
            setStones(stones.copy(stones = it))
        }
    }

    div {
        mContainer {
            mCard {
                mCardHeader(title = "existing stones : ${user?.login}") {

                }
                mList {

                    for (stone in stones.stones) {
                        mListItem {
                            mListItemIcon {
                                mIcon("drafts")
                            }
                            +stone.data.name
                        }
                    }
                }
            }
            mCard(raised = true) {
                mCardHeader(title = "add new stone") {

                }

                mTextField(label = "Stone Name", value = stones.newName, onChange = { event ->
                    setStones(stones.copy(newName = event.targetInputValue))
                }) {
                    //css(textField)
                }

                mCardActions {
                    mButton("add stone", MColor.primary, variant = MButtonVariant.contained, onClick = { _ ->
                        if (user != null) {
                            addStone(stones.newName, user)
                                .then {
                                    fetchStones().then {
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


fun fetchStones(): Promise<List<Stone>> =
    window.fetch("/api/stones")
        .then(Response::json)
        .then { it as Array<Stone> }
        .then { it.toList() }

fun addStone(name: String, user: User): Promise<Long> =
    StoneData(name, BigDecimal.of("5.4")).let { newStone ->
        window.fetch(
            "/api/stones", RequestInit(method = "POST",
                headers = Headers().apply {
                    set("Content-Type", "application/json")
                    set("Authorization", "Basic ${user.baseAuth()}")
                },
                body = JSON.stringify(newStone) { key, value ->
                    when (value) {
                        is BigDecimal -> value.toString()
                        else -> value
                    }
                })
        ).then { it.json().unsafeCast<Long>() }
    }


