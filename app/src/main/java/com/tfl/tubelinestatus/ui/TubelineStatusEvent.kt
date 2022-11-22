package com.tfl.tubelinestatus.ui

import com.tfl.tubelinestatus.models.TubelineStatusResponse

sealed class TubelineStatusEvent {
    data class ShowTubelineStatuses(val items: MutableList<TubelineStatusResponse>) : TubelineStatusEvent()
    data class ShowError(val errorCode: Int) : TubelineStatusEvent()
}
