import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.h1
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window
import kotlinx.coroutines.*
import react.dom.li
import react.dom.ul
import react.setState

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
                    + stone.data.name
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

