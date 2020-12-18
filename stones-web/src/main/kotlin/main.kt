import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.styles.Theme
import com.ccfraser.muirwik.components.styles.createMuiTheme
import kotlinx.browser.document
import kotlinx.browser.window
import react.RProps
import react.child
import react.dom.render
import react.functionalComponent
import react.useState
import services.loginUser

data class AppState (val user: User? = null, val loginDialog: Boolean = false) {
    fun loggedIn() : Boolean = user != null
}

data class User(val login : String , val pass: String? = null, val gtoken: String? = null) {
    fun baseAuth() =
        window.btoa("${login}:${pass}")

    fun oauthHeader() = "Bearer $gtoken"

    fun autHeader() = if (gtoken != null) {
        oauthHeader()
    } else {
        "Basic ${baseAuth()}"
    }

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
        if (user.pass != null) {
            loginUser(AppProps(stateWithUser, setAppState)).then {
                setAppState(
                    stateWithUser
                )
            }
        } else {
            //TODO
            println("setting state with user")
            setAppState(stateWithUser)
        }
    }) {

    }
}

