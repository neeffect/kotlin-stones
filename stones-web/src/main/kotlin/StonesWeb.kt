//import kotlinx.css.*
//import kotlinx.html.js.onClickFunction
import pl.setblack.kotlinStones.Stone
import react.RState



external interface AppState : RState {
    var stones: List<Stone>
}



