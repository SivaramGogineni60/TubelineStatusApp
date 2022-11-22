package com.tfl.tubelinestatus.models

data class LineStatus(
    val id: String,
    val lineId: String,
    val statusSeverity: String,
    val statusSeverityDescription: String,
    val reason: String,
    val validityPeriods: List<ValidityPeriod>,
    val disruption: Disruption,
)