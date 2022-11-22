package com.tfl.tubelinestatus.models

data class ValidityPeriod(
    val fromDate: String,
    val toDate: String,
    val isNow: String
)