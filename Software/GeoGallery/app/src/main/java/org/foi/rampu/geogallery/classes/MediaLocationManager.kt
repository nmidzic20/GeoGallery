package org.foi.rampu.geogallery.classes

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.foi.rampu.geogallery.R


class MediaLocationManager {

    fun saveLocationsFromSharedPrefsToAllLocationsInfo(context: Context, activity : Activity)
    {
        //since AllLocationsInfo will be lost anytime app not in use, first we must get this info from shared
        //preferences to AllLocationsInfo, then add new location to AllLocationsInfo, and then it will be copied back
        //to shared preferences

        var allSavedLocationsString : String? = null
        var allSavedLocations : MutableSet<SavedLocationInfo>? = null


        context?.getSharedPreferences("locations_preferences", Context.MODE_PRIVATE)?.apply {

            allSavedLocationsString = getString("all_locations_media_taken",
                activity.resources.getString(R.string.shared_prefs_default_location_info))

            if (allSavedLocationsString != activity.resources.getString(R.string.shared_prefs_default_location_info))
                allSavedLocations = Json.decodeFromString<MutableSet<SavedLocationInfo>>(allSavedLocationsString!!)
            else
                allSavedLocations = null

            Log.i("SHARED_1", allSavedLocations.toString())
        }

        if (allSavedLocations != null) AllLocationsInfo.savedLocationInfo = allSavedLocations!!

        AllLocationsInfo.savedLocationInfo.add(
            SavedLocationInfo(
                CurrentLocationInfo.locationInfo.value?.get("country").toString(),
                CurrentLocationInfo.locationInfo.value?.get("city").toString(),
                CurrentLocationInfo.locationInfo.value?.get("street").toString()
            )
        )
        Log.i("SHARED_ALL_LOC", allSavedLocations.toString())

        //now store locally on device in shared preferences

        //first convert to string
        val locationsListString = Json.encodeToString(AllLocationsInfo.savedLocationInfo)
        Log.i("CAMERA_ACTIVITY", locationsListString)

        context?.getSharedPreferences("locations_preferences", Context.MODE_PRIVATE)?.apply {

            edit().putString("all_locations_media_taken", locationsListString).apply()
            //check if saved
            var allSavedLocationsString : String? = null
            allSavedLocationsString = getString("all_locations_media_taken", activity.resources.getString(R.string.shared_prefs_default_location_info))
            Log.i("SHARED_2", allSavedLocationsString.toString())
        }
    }

    fun getLocationFromMediaName(mediaUri : Uri, activity: Activity) : SavedLocationInfo
    {
        var name = getFileName(mediaUri, activity)

        Log.i("DATA_NAME", name!!)

        var locationDataString = name?.split("___")
        var country = locationDataString?.get(1)?.split("_")?.get(0)
        var city = locationDataString?.get(2)?.split("_")?.get(0)
        var street = locationDataString?.get(3)?.split("_")?.get(0)

        var locationData = SavedLocationInfo(country!!,city!!,street!!)

        Log.i("DATA_NAME", "$name $country $city $street $locationData")
        return locationData
    }

    fun getFileName(uri: Uri, activity: Activity): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = activity.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}