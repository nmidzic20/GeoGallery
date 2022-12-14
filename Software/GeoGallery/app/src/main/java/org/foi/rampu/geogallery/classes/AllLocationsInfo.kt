package org.foi.rampu.geogallery.classes

import android.net.Uri
import kotlinx.serialization.Serializable

@Serializable
data class SavedLocationInfo(
    val country : String,
    val city : String,
    val street : String
)

@Serializable
object AllLocationsInfo {
    var savedLocationInfo : MutableList<SavedLocationInfo> = mutableListOf()
}

