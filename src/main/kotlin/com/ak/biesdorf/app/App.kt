package com.ak.biesdorf.app

import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotless.dsl.ktor.Kotless
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import io.ktor.response.respondBytes
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.flattenEntries
import java.nio.ByteBuffer

class Application : Kotless() {

    init {
        // To avoid cbor error
        System.setProperty("com.amazonaws.sdk.disableCbor", "true")
    }

    private val objectMapper = jacksonObjectMapper()
    private val kinesisClient = AmazonKinesisClientBuilder.standard()
            .withRegion(Config.AWS_REGION)
            .build()

    override fun prepare(app: Application) {
        app.routing {
            post("event") {
                // For production usage you would definitely like to validate the request is coming from Twilio.
                // More info here: https://www.twilio.com/docs/usage/security#validating-requests

                val params = call.receiveParameters().flattenEntries().toMap()
                val bytes = objectMapper.writeValueAsBytes(params)
                val partitionKey = params["EventType"] ?: "Unknown"

                kinesisClient.putRecord(
                        Config.STREAM_NAME,
                        ByteBuffer.wrap(bytes),
                        partitionKey
                )

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
