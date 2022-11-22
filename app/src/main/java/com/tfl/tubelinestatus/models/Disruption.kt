package com.tfl.tubelinestatus.models

data class Disruption(
    val category: String,
    val categoryDescription: String,
    val description: String,
    val created: String,
    val closureText: String
)