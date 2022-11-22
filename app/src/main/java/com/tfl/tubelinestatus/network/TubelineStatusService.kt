package com.tfl.tubelinestatus.network

import com.tfl.tubelinestatus.models.TubelineStatusResponse
import io.ktor.client.*
import io.ktor.client.request.*

class TubelineStatusService(
    private val httpClient: HttpClient,
    private val endpoint: String
) {
    suspend fun getTubelineStatus(): MutableList<TubelineStatusResponse> {
        return httpClient.get(endpoint) {
        }
    }
}