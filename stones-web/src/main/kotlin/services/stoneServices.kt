package services

import User
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import org.gciatto.kt.math.BigDecimal
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import pl.setblack.kotlinStones.StoneData
import pl.setblack.kotlinStones.StoneId
import pl.setblack.kotlinStones.StoneWithVotes
import kotlin.js.Promise


fun fetchStones(user: User?): Promise<List<StoneWithVotes>> =
    window.fetch("/api/stones", RequestInit(method = "GET",
        headers = Headers().apply {
            user?.let {
                set("Authorization", user.autHeader())
            }
        })
    )
        .then(Response::json)
        .then {
            Json.decodeFromDynamic<List<StoneWithVotes>>(it) }


fun addStone(newStone: StoneData, user: User): Promise<Long> =
    window.fetch(
        "/api/stones", RequestInit(method = "POST",
            headers = Headers().apply {
                set("Content-Type", "application/json")
                set("Authorization", user.autHeader())
            },
            body = JSON.stringify(newStone) { key, value ->
                when (value) {
                    is BigDecimal -> value.toString()
                    else -> value
                }
            })
    ).then { it.json().unsafeCast<Long>() }

fun voteStone(id:StoneId, user: User) : Promise<Unit> =
    window.fetch(
        "/api/stones/${id}/vote", RequestInit(method = "POST",
            headers = Headers().apply {
                set("Content-Type", "application/json")
                set("Authorization", user.autHeader())
            },
        )
    ).then { it.json().unsafeCast<Long>() }


