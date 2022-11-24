package com.tfl.tubelinestatus.ui

data class TubelineStatusViewState(
    val shouldShowLoadingMessage: Boolean = false,
    val tubelineStatus: List<TubelineStatusUIModel> = emptyList()
)
