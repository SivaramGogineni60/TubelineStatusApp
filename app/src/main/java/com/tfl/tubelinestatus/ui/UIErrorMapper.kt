package com.tfl.tubelinestatus.ui

import android.content.res.Resources
import com.tfl.tubelinestatus.R

class UIErrorMapper(private val resources: Resources) {
    fun mapErrorCodeToMessage(errorCode: Int): String {
        return if (errorCode == -1) {
            resources.getString(R.string.network_error)
        } else {
            resources.getString(R.string.unknown_error)
        }
    }
}