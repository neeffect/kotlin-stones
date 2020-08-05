
import react.dom.*
import kotlin.browser.document
//import kotlinx.css.*
//import kotlinx.html.js.onClickFunction
import styled.*
import react.*
import kotlin.browser.window


external interface AppState : RState {
    var currentName: String?
}
