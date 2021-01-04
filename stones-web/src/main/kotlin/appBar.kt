import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mIconButton
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.css.flexGrow
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.px
import react.functionalComponent
import styled.css

val appBar = functionalComponent<AppProps> { props ->
    mAppBar {
        mToolbar {
            mIconButton("menu", color = MColor.inherit, onClick = {
                document.location?.hash = ""
                window.history.pushState("", "Stones", "/")
            }) { css { marginLeft = -12.px; marginRight = 20.px }}

                mTypography("Stones", variant = MTypographyVariant.h6, color = MTypographyColor.inherit) {
                css { flexGrow = 1.0 }

            }
            if (props.state.loggedIn()) {
                mIconButton ("account_circle", color = MColor.inherit ) {
                    attrs.title = props.state.user?.login ?:"none"
                }

            } else {
                mButton("Login", color = MColor.inherit, onClick = {
                    props.stateChange(props.state.copy(loginDialog = true))
                })
            }
        }
    }
}




