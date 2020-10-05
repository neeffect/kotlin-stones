import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogActions
import com.ccfraser.muirwik.components.dialog.mDialogContent
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.mTextField
import com.ccfraser.muirwik.components.targetInputValue
import kotlinx.html.InputType
import react.RProps
import react.functionalComponent
import react.useState

data class LoginDialog(val opened: Boolean = false, val setUser : (User) -> Unit) : RProps

val loginDialog = functionalComponent<LoginDialog> { props ->
    val (login, setLogin) = useState("")
    val (password, setPassword) = useState("")
    mDialog(props.opened) {
        mDialogTitle("Login")
        mDialogContent {
            mTextField(label = "login", value = login, onChange = {event -> setLogin(event.targetInputValue)} )
            mTextField(label="password", type = InputType.password,
                value =  password, onChange = {event -> setPassword(event.targetInputValue)} )
        }
        mDialogActions {
            mButton("login", onClick = {
                props.setUser( User(login,password))

            })
        }
    }
}
