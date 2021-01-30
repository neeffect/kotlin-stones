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

    val colorPickerWrapper by css {
        margin = 2.em.toString()
        display = Display.inlineBlock
    }

    val overflowCard by css {
        color = Color("#111111")

        +"MuiCard-root" {
            overflow = Overflow.visible
        }
    }
}
