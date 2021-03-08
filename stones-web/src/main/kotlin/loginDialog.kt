import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogActions
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import kotlinx.browser.window
import kotlinx.css.BackgroundRepeat
import kotlinx.css.Color
import kotlinx.css.Image
import kotlinx.css.TextTransform
import kotlinx.css.backgroundColor
import kotlinx.css.backgroundImage
import kotlinx.css.backgroundPosition
import kotlinx.css.backgroundRepeat
import kotlinx.css.color
import kotlinx.css.fontSize
import kotlinx.css.height
import kotlinx.css.paddingLeft
import kotlinx.css.px
import kotlinx.css.textTransform
import kotlinx.serialization.Serializable
import org.w3c.dom.url.URLSearchParams
import react.RProps
import react.functionalComponent
import react.useEffect
import react.useState
import services.getGithubOauthUrl
import services.getGoogleOauthUrl
import services.loginUser
import services.redirectUri
import styled.css

data class LoginDialog(val opened: Boolean = false, val setUser: (User) -> Unit) : RProps

@Suppress("MagicNumber")
val loginDialog = functionalComponent<LoginDialog> { props ->
    val (login, setLogin) = useState("")
    val (password, setPassword) = useState("")
    /*explanation - useEffect is needed to call state change AFTER rendering
     state change during render is a not really good idea in react
     first param of useEffect is a list of "watchers" if any of this changes - inner function will be called again
     in that case we want to check oauth param only once after page loads- hence the empty list
    */
    useEffect(emptyList()) {
        checkOauthLogin(props)
    }


    mDialog(props.opened) {
        mDialogTitle("Login")

        mDialogActions {

            mButton("Sign in with Github",
                color = MColor.secondary,
                variant = MButtonVariant.contained,
                onClick = {
                    getGithubOauthUrl().then { url ->
                        window.location.assign(url)
                    }
                }) {
                css {
                    +StonesStyles.googleLogin
                    +"MuiButtonBase-root" {
                        backgroundImage = Image("url(/icons/github/GitHub-Mark-32px.png)")
                        backgroundRepeat = BackgroundRepeat.noRepeat
                        backgroundPosition = "2% 50%"
                        backgroundColor = Color("#ffffff")
                        paddingLeft = 24.px
                        fontSize = 13.px
                        height = 40.px
                        textTransform = TextTransform.none
                        color = Color("#666666")
                    }

                }
            }
            mButton("", onClick = {
                getGoogleOauthUrl().then { url ->
                    window.location.assign(url)
                }
            }) {
                css {
                    +StonesStyles.googleLogin
                    backgroundImage = Image("url(/icons/btn_google_signin_light_normal_web.png)")
                }

            }
        }
    }
}

fun checkOauthLogin(props: LoginDialog) {
    val location = window.location.href
    if (location.contains("?auth=")) {
        val searchParams = URLSearchParams(window.location.search.substring(1))
        val state = searchParams.get("state")?.replace(" ", "+")
        val code = searchParams.get("code")
        val provider = searchParams.get("auth")!!
        val data = LocalOauthLoginData(code!!, state!!, redirectUri(provider))
        loginUser(provider, data).then { jwtLogin ->
            window.history.pushState("", "Stones (logged)", "/")
            props.setUser(User(jwtLogin.displayName, gtoken = jwtLogin.encodedToken))
        }
    }
}

@Serializable
data class LocalOauthLoginData(
    val code: String,
    val state: String,
    val redirectUri: String
)


