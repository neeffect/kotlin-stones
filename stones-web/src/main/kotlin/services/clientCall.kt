package services

import AppProps
import kotlinx.browser.window
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.Promise

fun loginUser(appProps: AppProps): Promise<Unit> =
    fetchData(url = "/sys/currentUser", appProps = appProps)
        .then { user -> println(user) }


fun fetchData(url: String, appProps: AppProps) =
    services.fetchData<Any>(url, appProps)

fun <T> fetchData(url: String, appProps: AppProps) =
    window
        .fetch(url, RequestInit(
            headers = Headers().apply {
                set("Content-Type", "application/json")
                if (appProps.state.loggedIn()) {
                    set("Authorization", "Basic ${appProps.state.user?.baseAuth()}")
                }
            }
        ))
        .then(Response::json, { error -> println(error) })

