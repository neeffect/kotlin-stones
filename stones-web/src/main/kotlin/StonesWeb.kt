//import kotlinx.css.*
//import kotlinx.html.js.onClickFunction
import react.RState
import org.gciatto.kt.math.BigDecimal


external interface AppState : RState {
    var stones: List<Stone>
}


data class Stone(val id: Long, val data: StoneData)

data class StoneData(val name: String, val price: BigDecimal)

