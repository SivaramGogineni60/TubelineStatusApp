package com.tfl.tubelinestatus.repositories.api

import com.tfl.tubelinestatus.models.TubelineStatusResponse
import com.tfl.tubelinestatus.network.NetworkResponse
import com.tfl.tubelinestatus.network.TubelineStatusDataSource

class TubelineStatusRepositoryImpl(
    private val tubelineStatusDataSource: TubelineStatusDataSource
) : TubelineStatusRepository {
    override suspend fun getAllTubelineStatuses(): NetworkResponse<MutableList<TubelineStatusResponse>> {
        return tubelineStatusDataSource.getTubelineStatus()
    }
}