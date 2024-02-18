package org.foi.rampu.geogallery.classes

import androidx.lifecycle.MutableLiveData

object CurrentLocationInfo {

    var locationInfo : MutableLiveData<MutableMap<String, String>> = MutableLiveData(
        mutableMapOf(
            "country" to "",
            "city" to "",
            "street" to ""
        )
    )
}