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

external fun encodeURIComponent(uri: String): String

val baseUrl = window.location.protocol + "//" + window.location.host

fun redirectUri(provider: String) = "$baseUrl?auth=$provider"

fun redirectUriEncoded(provider: String) = encodeURIComponent(redirectUri(provider))

fun getGoogleOauthUrl(): Promise<String> =
    MainScope().promise {
        val client = HttpClient(Js)
        val redirect = redirectUriEncoded("google")
        val resp = client.get<String>("${baseUrl}/api/oauth/generateUrl/Google?redirect=$redirect")
        println(resp)
        resp
    }

fun getGithubOauthUrl(): Promise<String> =
    MainScope().promise {
        val client = HttpClient(Js)
        val redirect = redirectUriEncoded("github")
        val resp = client.get<String>("${baseUrl}/api/oauth/generateUrl/Github?redirect=$redirect")
        println(resp)
        resp
    }



fun loginUser(provider: String, data: LocalOauthLoginData): Promise<JwtLogin> = MainScope().promise {
    val client = HttpClient(Js) {
        install(JsonFeature) {
            serializer =  KotlinxSerializer()
        }
    }
    val providerName = provider.capitalize()
    val resp = client.post<JwtLogin>("${baseUrl}/api/oauth/loginUser/$providerName") {
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
