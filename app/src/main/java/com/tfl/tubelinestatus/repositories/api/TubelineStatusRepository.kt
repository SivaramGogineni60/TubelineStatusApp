package com.tfl.tubelinestatus.repositories.api

import com.tfl.tubelinestatus.models.TubelineStatusResponse
import com.tfl.tubelinestatus.network.NetworkResponse

interface TubelineStatusRepository {
    suspend fun getAllTubelineStatuses(): NetworkResponse<MutableList<TubelineStatusResponse>>
}