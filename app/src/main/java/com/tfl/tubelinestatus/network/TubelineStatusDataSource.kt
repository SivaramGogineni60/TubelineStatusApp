package com.tfl.tubelinestatus.network

import com.tfl.tubelinestatus.models.TubelineStatusResponse
import com.tfl.tubelinestatus.utils.NetworkErrorParser

class TubelineStatusDataSource(
    private val tubelineStatusService: TubelineStatusService,
    private val networkErrorParser: NetworkErrorParser
) {
    suspend fun getTubelineStatus(): NetworkResponse<MutableList<TubelineStatusResponse>> {
        return try {
            NetworkResponse.Success(tubelineStatusService.getTubelineStatus())
        } catch (e: Throwable) {
            NetworkResponse.Error(networkErrorParser.checkError(e))
        }
    }
}