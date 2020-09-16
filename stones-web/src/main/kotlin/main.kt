import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemIcon
import com.ccfraser.muirwik.components.styles.Theme
import com.ccfraser.muirwik.components.styles.createMuiTheme
import kotlinx.browser.document
import kotlinx.browser.window

import kotlinx.coroutines.*

import kotlinx.html.js.onClickFunction
import org.gciatto.kt.math.BigDecimal
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import pl.setblack.kotlinStones.Stone
import pl.setblack.kotlinStones.StoneData
import react.*
import react.dom.*
import kotlin.js.Promise

fun main() {
    render(document.getElementById("root")) {


        val themeOptions :dynamic = js ("{}")
        themeOptions.palette = js("{}")
        themeOptions.palette.primary = js("{}")
        themeOptions.palette.primary.main = "#ffee58"
        themeOptions.palette.secondary = js("{}")
        themeOptions.palette.secondary.main = "#29b6f6"
        val theme: Theme = createMuiTheme(themeOptions)
        mThemeProvider(theme){
            child(app) {}
        }

    }
}

data class  StonesState(val stones: List<Stone> = listOf(), val newName:String = "")



val app = functionalComponent<RProps> {
    val (stones, setStones) = useState (StonesState())



    useEffect(emptyList()) {
        fetchStones().then {
            setStones(stones.copy(stones  = it))
        }
    }

    div {
        h1 {
            +"Here I am.(F1)"
        }


        button {
            +"Add stone"
            attrs {
                onClickFunction = { _ ->
                    val mainScope = MainScope()
                    mainScope.launch {
                        addStone("no name")
                        fetchStones().then {
                            setStones(stones.copy(stones  = it))
                        }
                    }
                }
            }
        }

        mContainer{
            mCard {
                mCardHeader(title = "existing stones") {

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
                mCardHeader(title = "add new stone")  {

                }

                mTextField(label = "Stone Name", value = stones.newName,  onChange = { event ->
                    setStones(stones.copy(newName = event.targetInputValue))
                }) {
                    //css(textField)
                }

                mCardActions {
                    mButton("add stone", MColor.primary,variant = MButtonVariant.contained,onClick = { _ ->
                        val mainScope = MainScope()
                        mainScope.launch {
                            addStone(stones.newName)
                            fetchStones().then {
                                setStones(stones.copy(stones  = it))
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

suspend fun addStone(name:String): Long = coroutineScope {
    async {
        val newStone = StoneData(name, BigDecimal.of("5.4"))
        window.fetch(
            "/api/stones", RequestInit(method = "POST",
                headers = Headers().apply {
                    set("Content-Type", "application/json")
                    set("Authorization", "Basic ZWRpdG9yOmVkaXRvcg==")
                },
                body = JSON.stringify(newStone) { key, value ->
                    when (value) {
                        is BigDecimal -> value.toString()
                        else -> value
                    }
                })
        )
            .await()
            .json()
            .await()
            .unsafeCast<Long>()
    }.await()
}