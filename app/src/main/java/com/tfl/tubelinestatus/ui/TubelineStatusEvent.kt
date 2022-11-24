package com.tfl.tubelinestatus.ui

sealed class TubelineStatusEvent {
    data class ShowError(val errorMessage: String) : TubelineStatusEvent()
}
