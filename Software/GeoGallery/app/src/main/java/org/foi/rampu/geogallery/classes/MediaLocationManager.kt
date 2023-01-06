package org.foi.rampu.geogallery.classes

import android.app.Activity
import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.ws.LocationInfoManager

class MediaLocationManager {

    fun saveLocation(uri : Uri, context : Context, activity : Activity) {

        //HomeActivity locations callback automatically updates current location and saves it to CurrentLocationInfo object
        //here as we save the just taken picture/video, we need to access CurrentLocationInfo,
        // save it to AllLocationsInfo object to keep track of all the locations where any media was recorded/taken,
        //and save AllLocationsInfo to shared preferences (isolated storage) to keep it when the app is not in use,
        //then save current location to this picture's metadata (UserInfo)
        // so we can determine the right folder when displaying this picture

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
            allSavedLocationsString = getString("all_locations_media_taken", activity.resources.getString(R.string.shared_prefs_default_location_info))
            Log.i("SHARED_2", allSavedLocationsString.toString())
        }

        //metadata

        var data = CurrentLocationInfo.locationInfo.value

        Log.i("DATA", data.toString())

        var dataString = Json.encodeToString(data)

        var exifData = ExifInterface(activity.contentResolver.openFileDescriptor(uri, "rw", null)!!.fileDescriptor)
        exifData.setAttribute("UserComment", dataString)
        exifData.saveAttributes()

        Log.i("EXIF", getTagString("UserComment", exifData).toString() + " " + uri.toString())


        //store location info in media name itself


    }

    private fun getTagString(tag: String, exif: ExifInterface): String?
    {
        return """$tag : ${exif.getAttribute(tag)}"""
    }


    fun getLocationMetadata(fragment : Fragment, mediaUri : Uri) : SavedLocationInfo
    {
        var exifData = ExifInterface(fragment!!.requireActivity().contentResolver!!
            .openFileDescriptor(mediaUri, "rw", null)!!.fileDescriptor)

        var locationMetadataString = exifData.getAttribute("UserComment")
        Log.i("IMAGE_USER_COMMENT", locationMetadataString.toString())

        var locationMetadata = Json.decodeFromString<SavedLocationInfo>(locationMetadataString!!)

        return locationMetadata
    }
}