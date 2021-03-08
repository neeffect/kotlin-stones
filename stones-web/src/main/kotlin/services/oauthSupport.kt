package services

import LocalOauthLoginData
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.serialization.Serializable
import kotlin.js.Promise

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
