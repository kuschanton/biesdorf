package com.ak.biesdorf.app

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotless.dsl.ktor.Kotless
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.flattenEntries

class Application : Kotless() {

    private val objectMapper = jacksonObjectMapper()

    override fun prepare(app: Application) {
        app.routing {
            get("/") {
                call.respondText { "Up and running!" }
            }

            post("event") {
                // For production usage you would definitely like to validate the request is coming from Twilio.
                // More info here: https://www.twilio.com/docs/usage/security#validating-requests

                val params = call.request.queryParameters.flattenEntries().toMap()
                val json = objectMapper.writeValueAsString(params)
                println(json)
                // Send event to Kinesis

                call.respondNoContent()
            }
        }
    }

    companion object {

        /**
         * From Twilio TaskRouter documentation (https://www.twilio.com/docs/taskrouter/api/event#event-callbacks):
         * "Your application should respond to Event Callbacks with 204 No Content and
         * a Content-Type header of "application/json" within 15 seconds."
         */
        suspend fun ApplicationCall.respondNoContent() = respondBytes(
                ByteArray(0),
                ContentType.Application.Json,
                HttpStatusCode.NoContent
        )
    }

}
