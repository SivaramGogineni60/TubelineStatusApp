package com.tfl.tubelinestatus.models

data class TubelineStatusResponse(
    val id: String,
    val name: String,
    val lineStatuses: List<LineStatus>
)