import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.Overflow
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.em
import kotlinx.css.fontFamily
import kotlinx.css.height
import kotlinx.css.margin
import kotlinx.css.overflow
import kotlinx.css.px
import kotlinx.css.width
import styled.StyleSheet

@Suppress("MagicNumber")
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
