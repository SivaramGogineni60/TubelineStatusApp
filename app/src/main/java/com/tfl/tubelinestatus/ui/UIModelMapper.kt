package com.tfl.tubelinestatus.ui

import android.content.res.Resources
import com.tfl.tubelinestatus.R
import com.tfl.tubelinestatus.models.TubelineStatusResponse

class UIModelMapper(private val resources: Resources) {
    fun mapNetworkResponseToUIModel(
        items: MutableList<TubelineStatusResponse>
    ): List<TubelineStatusUIModel> {
        val tubelineStatusList = ArrayList<TubelineStatusUIModel>()
        for (status in items) {
            tubelineStatusList.add(
                TubelineStatusUIModel(
                    mapColorCode(status.id),
                    status.name,
                    status.lineStatuses.get(0).statusSeverityDescription
                )
            )
        }
        return tubelineStatusList
    }

    private fun mapColorCode(tubelineId: String): Int {
        val colorCodeMap: Map<String,Int> = mapOf<String,Int>(
            "bakerloo" to resources.getColor(R.color.bakerloo),
            "central" to resources.getColor(R.color.central),
            "circle" to resources.getColor(R.color.circle),
            "district" to resources.getColor(R.color.district),
            "hammersmith-city" to resources.getColor(R.color.hammersmith_city),
            "jubilee" to resources.getColor(R.color.jubilee),
            "metropolitan" to resources.getColor(R.color.metropolitan),
            "northern" to resources.getColor(R.color.northern),
            "piccadilly" to resources.getColor(R.color.piccadilly),
            "victoria" to resources.getColor(R.color.victoria),
            "waterloo-city" to resources.getColor(R.color.waterloo_city)
        )
        colorCodeMap[tubelineId]
        return colorCodeMap[tubelineId] ?: resources.getColor(R.color.white)
    }
}