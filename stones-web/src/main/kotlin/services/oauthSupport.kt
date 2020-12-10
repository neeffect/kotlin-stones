package services

import LocalOauthLoginData
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlin.js.Promise
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.Serializable

val baseUrl = window.location.protocol + "//" + window.location.host

fun getGoogleOauthUrl(): Promise<String> =
    MainScope().promise {
        val client = HttpClient(Js)
        val resp = client.get<String>("${baseUrl}/api/oauth/generateUrl/Google?redirect=http://localhost:8080")
        println(resp)
        resp
    }


fun loginUser(data: LocalOauthLoginData): Promise<JwtLogin> = MainScope().promise {
    val client = HttpClient(Js) {
        install(JsonFeature) {
            serializer =  KotlinxSerializer()
        }
    }
    val resp = client.post<JwtLogin>("${baseUrl}/api/oauth/loginUser/Google") {
        body = data
        header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    }
    resp
}

@Serializable
data class JwtLogin(
    val encodedToken: String,
    val displayName: String,
    val subject: String
)
