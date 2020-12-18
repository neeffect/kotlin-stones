import kotlinx.css.*
import styled.StyleSheet

object StonesStyles : StyleSheet("StoneStyles", isStatic = true) {
    val robotFont  by css {
        fontFamily = "'Roboto', sans-serif"
    }

    val googleLogin by css {

        +"MuiButtonBase-root" {
           width = 191.px
            height = 46.px
        }
    }
}
