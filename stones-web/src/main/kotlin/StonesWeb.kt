//import kotlinx.css.*
//import kotlinx.html.js.onClickFunction
import react.RState


external interface AppState : RState {
    var stones: List<Stone>
}


data class Stone(val id: Long, val name: String)
