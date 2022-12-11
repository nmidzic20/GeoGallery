package org.foi.rampu.geogallery.ws

import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.viewModelFactory
import org.foi.rampu.geogallery.GalleryActivity
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.fragments.LocationInfoFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationInfoManager(val locationInfoFragment: LocationInfoFragment) {

    fun loadLocationInfo(location: String) {

        //first check in WsLocationInfoResultList, then load from web if none found in list for that location
        var foundLocationInfo : Boolean = displayLocationInfo(location)
        Log.i("FOLDER", foundLocationInfo.toString())

        if (!foundLocationInfo)
            fetchLocationInfoFromWeb(location)

    }

    fun saveLocationInfo(title : String, extract: String)
    {
        val newLocationInfo = WsLocationInfoResult(title, extract)
        WsLocationInfoResultList.results.add(newLocationInfo)
    }

    fun displayLocationInfo(location : String) : Boolean
    {
        val locationInfo = WsLocationInfoResultList.results.firstOrNull{ it.title == location }
        val paragraph = locationInfo?.extract?.split("\r?\n|\r".toRegex())?.get(0)
        locationInfoFragment.view?.findViewById<TextView>(R.id.tv_location_info)?.text = paragraph

        return locationInfo != null;

    }

    private fun displayWebServiceErrorMessage() {
        Toast.makeText(
            locationInfoFragment.context,
            R.string.news_ws_err_msg,
            Toast.LENGTH_LONG
        ).show()
    }

    fun fetchLocationInfoFromWeb(location: String)
    {
        val ws = WsLocationInfo.locationInfoService

        ws.getLocationInfo(location).enqueue(
            object : Callback<WsResponse>
            {
                override fun onResponse(call: Call<WsResponse>?, response: Response<WsResponse>?)
                {
                    if (response?.isSuccessful == true)
                    {
                        val responseBody = response.body()
                        //since pages is a map, get the only key from set of keys to access extract with it from the map "pages"
                        var mapKey = responseBody.query?.pages?.keys?.elementAt(0)
                        Log.i("INFO KEY", mapKey.toString())

                        var title = responseBody.query?.pages?.get(mapKey)?.title
                        var extract = responseBody.query?.pages?.get(mapKey)?.extract

                        if (extract == null) extract = "Not fetched"
                        Log.i("INFO", responseBody.toString())

                        saveLocationInfo(title!!,extract!!)
                        displayLocationInfo(location)

                        locationInfoFragment.view?.findViewById<ProgressBar>(R.id.pb_location_info_loading)?.isVisible = false

                    }
                    else
                    {
                        displayWebServiceErrorMessage()
                        Log.i("ERR", "On Response error")
                    }
                }
                override fun onFailure(call: Call<WsResponse>?, t: Throwable?)
                {
                    displayWebServiceErrorMessage()
                    Log.i("ERR", "On Failure error")
                }
            }
        )
    }
}