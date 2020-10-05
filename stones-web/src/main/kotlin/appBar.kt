import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mIconButton
import kotlinx.browser.window
import kotlinx.css.flexGrow
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.px
import org.w3c.fetch.Response
import pl.setblack.kotlinStones.Stone
import react.RProps
import react.functionalComponent
import styled.css
import kotlin.js.Promise

val appBar = functionalComponent<AppProps> { props ->
    mAppBar {
        mToolbar {
            mIconButton("menu", color = MColor.inherit) { css { marginLeft = -12.px; marginRight = 20.px }}
            mTypography("Title", variant = MTypographyVariant.h6, color = MTypographyColor.inherit) {
                css { flexGrow = 1.0 }
            }
            if (props.state.loggedIn()) {
                mIconButton ("account_circle", color = MColor.inherit )
//                            mMenu(true, anchorEl = ) {  }
            } else {
                mButton("Login", color = MColor.inherit, onClick = {
                    props.stateChange(props.state.copy(loginDialog = true))
                })
            }
        }
    }
}




