package org.foi.rampu.geogallery.ws

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewmodel.viewModelFactory
import org.foi.rampu.geogallery.GalleryActivity
import org.foi.rampu.geogallery.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationInfoManager(val activity : GalleryActivity) {

    private val ws = WsLocationInfo.locationInfoService
    //var extract : String? = null

    fun loadLocationInfo(location: String) {

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

                        if (extract == null) extract = "Nije dohvaceno"
                        Log.i("INFO", responseBody.toString())

                        saveLocationInfo(title!!,extract!!)
                        displayLocationInfo(location)

                    }
                    else
                    {
                        displayWebServiceErrorMessage()
                        Log.i("ERR", "Greska on Response")
                    }
                }
                override fun onFailure(call: Call<WsResponse>?, t: Throwable?)
                {
                    displayWebServiceErrorMessage()
                    Log.i("ERR", "Greska on Failure")
                }
            }
        )
    }

    fun saveLocationInfo(title : String, extract: String)
    {
        val newLocationInfo = WsLocationInfoResult(title, extract)
        WsLocationInfoResultList.results = ArrayList<WsLocationInfoResult>()
        WsLocationInfoResultList.results.add(newLocationInfo)
    }

    fun displayLocationInfo(location : String)
    {
        val locationInfo = WsLocationInfoResultList.results.firstOrNull{ it.title == location }
        val paragraph = locationInfo?.extract?.split("\r?\n|\r".toRegex())?.get(0)
        //activity.viewBinding.tvLocationInfo.text = paragraph

    }

    private fun displayWebServiceErrorMessage() {
        Toast.makeText(
            activity,
            R.string.news_ws_err_msg,
            Toast.LENGTH_LONG
        ).show()
    }
}