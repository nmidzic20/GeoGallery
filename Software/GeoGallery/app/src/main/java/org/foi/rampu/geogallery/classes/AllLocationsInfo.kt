package org.foi.rampu.geogallery.classes

import android.net.Uri

data class SavedLocationInfo(
    val country : String,
    val city : String,
    val street : String,
    val uri : Uri
)

object AllLocationsInfo {
    var savedLocationInfo : MutableList<SavedLocationInfo> = mutableListOf()
}

