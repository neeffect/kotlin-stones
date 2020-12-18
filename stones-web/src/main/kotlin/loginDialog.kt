import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogActions
import com.ccfraser.muirwik.components.dialog.mDialogContent
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.mSvgIcon
import com.ccfraser.muirwik.components.mTextField
import com.ccfraser.muirwik.components.targetInputValue
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.browser.window
import kotlinx.html.InputType
import react.RProps
import react.functionalComponent
import react.useState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlinx.css.Image
import kotlinx.css.backgroundImage
import kotlinx.html.classes
import kotlinx.serialization.Serializable
import org.w3c.dom.url.URLSearchParams
import react.dom.img
import react.useEffect
import services.baseUrl
import services.getGoogleOauthUrl
import services.loginUser
import styled.css
import styled.styledI

data class LoginDialog(val opened: Boolean = false, val setUser: (User) -> Unit) : RProps

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
//        mDialogContent {
//            mTextField(label = "login", value = login, onChange = { event -> setLogin(event.targetInputValue) })
//            mTextField(label = "password", type = InputType.password,
//                value = password, onChange = { event -> setPassword(event.targetInputValue) })
//        }
        mDialogActions {
//            mButton("login", onClick = {
//                props.setUser(User(login, password))
//
//            })

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
    if (location.contains("?state=")) {
        val searchParams = URLSearchParams(window.location.search.substring(1))
        val state = searchParams.get("state")?.replace(" ", "+")
        val code = searchParams.get("code")
        val data = LocalOauthLoginData(code!!, state!!, baseUrl)
        loginUser(data).then { jwtLogin ->
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

