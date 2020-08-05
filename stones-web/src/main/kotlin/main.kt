import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.h1
import react.dom.render
import kotlin.browser.document

fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
    }
}

class App : RComponent<RProps, AppState>() {
    override fun RBuilder.render() {
        h1 {
            +"Here I am."
        }
    }
}
