package com.tfl.tubelinestatus.network

import android.util.Log
import com.tfl.tubelinestatus.managers.SessionManager
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.http.*

class TubelineStatusHttpClient {

    private val client = HttpClient(CIO) {

        //Header
        install(DefaultRequest) {
            header("Accept", "application/json")
            header("Content-type", "application/json")
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${SessionManager.userToken}")
        }
        // Json
        install(JsonFeature) {
            serializer = GsonSerializer()
        }

        // Timeout
        install(HttpTimeout) {
            requestTimeoutMillis = 15000L
            connectTimeoutMillis = 15000L
            socketTimeoutMillis = 15000L
        }

        //Now you see response logs inside terminal
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }

        //Print other logs
        install(ResponseObserver) {
            onResponse { response ->
                Log.d("ApiService", "HTTP status: ${response.status.value}")
            }
        }

    }

    fun getClient(): HttpClient {
        return client
    }
}