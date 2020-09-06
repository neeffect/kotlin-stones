import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.mContainer
import kotlinx.browser.document
import kotlinx.browser.window

import kotlinx.coroutines.*

import kotlinx.html.js.onClickFunction
import org.gciatto.kt.math.BigDecimal
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import react.*
import react.dom.*
import kotlin.js.Promise

fun main() {
    render(document.getElementById("root")) {
        child(app) {}
    }
}

data class  StonesState(val stones: List<Stone> = listOf())



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
                        addStone()
                        fetchStones().then {
                            setStones(stones.copy(stones  = it))
                        }
                    }
                }
            }
        }

        mContainer{
            mCard {
                ul {

                    for (stone in stones.stones) {
                        li {
                            +stone.data.name
                        }
                    }
                }
            }
            mCard  {
                mCardHeader {
                    title("add new stone")
                }
            }

        }
    }

}
//
//class App : RComponent<RProps, AppState>() {
//
//    override fun AppState.init() {
//        stones = listOf()
//
//        fetchStones()
//            .then { loadedStones ->
//                setState {
//                    stones = loadedStones
//                }
//            }
//
//    }
//
//    override fun RBuilder.render() {
//        h1 {
//            +"Here I am."
//        }
//        ul {
//
//            for (stone in state.stones) {
//                li {
//                    +stone.data.name
//                }
//            }
//        }
//
//        button {
//            +"Add stone"
//            attrs {
//                onClickFunction = { _ ->
//                    val mainScope = MainScope()
//                    mainScope.launch {
//                        addStone()
//                    }
//                }
//            }
//        }
//    }
//}


fun fetchStones(): Promise<List<Stone>> =
    window.fetch("/api/stones")
        .then(Response::json)
        .then { it as Array<Stone> }
        .then { it.toList() }

suspend fun addStone(): Long = coroutineScope {
    async {
        val newStone = StoneData("some", BigDecimal(5.4))
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
