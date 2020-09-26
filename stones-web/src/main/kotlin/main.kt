import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemIcon
import com.ccfraser.muirwik.components.styles.Theme
import com.ccfraser.muirwik.components.styles.createMuiTheme
import kotlinx.browser.document
import kotlinx.browser.window

import kotlinx.coroutines.*

import kotlinx.html.js.onClickFunction
import org.gciatto.kt.math.BigDecimal
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import pl.setblack.kotlinStones.Stone
import pl.setblack.kotlinStones.StoneData
import react.*
import react.dom.*
import kotlin.js.Promise

//external interface AppProps: RProps {
//    var user: User
//}
data class AppProps (val user: User) : RProps
data class User(val login : String , val pass: String)


fun main() {
    render(document.getElementById("root")) {

        val themeOptions :dynamic = js ("{}")
        themeOptions.palette = js("{}")
        themeOptions.palette.primary = js("{}")
        themeOptions.palette.primary.main = "#ffee58"
        themeOptions.palette.secondary = js("{}")
        themeOptions.palette.secondary.main = "#29b6f6"
        val theme: Theme = createMuiTheme(themeOptions)

        val appState  = AppProps(User("nono1", "nono2"))
        mThemeProvider(theme){
            child(app, appState) {
               // this.attrs.user = User("nono", "nono")
            }
        }

    }
}



val app = functionalComponent<AppProps> {props->

    child(stonesList, props) {

    }

}

