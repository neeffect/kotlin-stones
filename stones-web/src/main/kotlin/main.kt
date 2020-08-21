import react.RBuilder
import react.RComponent
import react.RProps
import kotlin.browser.document
import kotlin.browser.window
import kotlinx.coroutines.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.gciatto.kt.math.BigDecimal
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.dom.*
import react.setState
import kotlin.js.Promise

fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
    }
}

class App : RComponent<RProps, AppState>() {

    override fun AppState.init() {
        stones = listOf()
        val mainScope = MainScope()
        mainScope.launch {
            val loadedStones = fetchStones()
            setState {
                stones = loadedStones
            }
        }

    }

    override fun RBuilder.render() {
        h1 {
            +"Here I am."
        }
        ul {

            for (stone in state.stones) {
                li {
                    +stone.data.name
                }
            }
        }

        button {
            +"Add stone"
            attrs {
                onClickFunction = { _ ->
                    val mainScope = MainScope()
                    mainScope.launch {
                        addStone()
                    }
                }
            }
        }
    }
}


suspend fun fetchStones(): List<Stone> = coroutineScope {
    async {
        window.fetch("/api/stones")
            .await()
            .json()
            .await()
            .unsafeCast<Array<Stone>>()
    }.await().toList() //this await seems stupid
}



suspend fun addStone(): Long = coroutineScope {
    async {
        val newStone = StoneData("some", BigDecimal(5.4))
        window.fetch("/api/stones", RequestInit(method = "POST",
            headers = Headers().apply {
                set("Content-Type", "application/json")
                set("Authorization","Basic ZWRpdG9yOmVkaXRvcg==")
            },
            body = JSON.stringify(newStone) { key, value ->
                when (value ) {
                    is BigDecimal -> value.toString()
                        else ->  value
                }
            } ))
            .await()
            .json()
            .await()
            .unsafeCast<Long>()
    }.await()
}
