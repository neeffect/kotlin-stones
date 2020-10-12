import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemIcon
import com.ccfraser.muirwik.components.styles.Theme
import com.ccfraser.muirwik.components.styles.createMuiTheme
import kotlinx.browser.document
import kotlinx.browser.window

import kotlinx.coroutines.*
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.px
import kotlinx.html.InputType

import kotlinx.html.js.onClickFunction
import org.gciatto.kt.math.BigDecimal
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import pl.setblack.kotlinStones.Stone
import pl.setblack.kotlinStones.StoneData
import react.*
import react.dom.*
import services.loginUser
import styled.css
import kotlin.js.Promise

data class AppState (val user: User? = null, val loginDialog: Boolean = false) {
    fun loggedIn() : Boolean = user != null
}

data class User(val login : String , val pass: String) {
    fun baseAuth() =
        window.btoa("${login}:${pass}")
}

data class AppProps(val state: AppState, val stateChange : (AppState)->Unit) : RProps

fun main() {
    render(document.getElementById("root")) {

        val themeOptions :dynamic = js ("{}")
        themeOptions.palette = js("{}")
        themeOptions.palette.primary = js("{}")
        themeOptions.palette.primary.main = "#ffee58"
        themeOptions.palette.secondary = js("{}")
        themeOptions.palette.secondary.main = "#29b6f6"
        val theme: Theme = createMuiTheme(themeOptions)


        mThemeProvider(theme){
            child(app) {

            }
        }

    }
}

val app = functionalComponent<RProps> {props->
    val (appState, setAppState) = useState (AppState())

    child(appBar, AppProps(appState, setAppState)) {

    }
    child(stonesList, AppProps(appState, setAppState)) {

    }
    child(loginDialog, LoginDialog(appState.loginDialog) { user ->
        val stateWithUser = appState.copy(user = user, loginDialog = false)
        loginUser(AppProps(stateWithUser, setAppState)).then {
            println("logged")
            setAppState(
                stateWithUser
            )
        }
    }) {

    }
}

