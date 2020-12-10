package pl.setblack.kstones.oauth
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.util.*

@Suppress("EXPERIMENTAL_API_USAGE")
object GoogleOauth {
    val pattern = """Bearer\s(.*)""".toRegex()
    suspend fun findUser(authHeader: String) {
        val match =pattern.matchEntire(authHeader)
        if (match != null) {
            val token = match.groups.get(1)?.value
            if (token != null) {
                HttpClient(CIO).use {client ->

                    val result = client.get<String>(
                         "https://www.googleapis.com/oauth2/v1/userinfo?alt=json"
                    ) {
                        header("Authorization", authHeader)
                    }
                    println(result)
                    result
                }
            }

        }
    }

}
