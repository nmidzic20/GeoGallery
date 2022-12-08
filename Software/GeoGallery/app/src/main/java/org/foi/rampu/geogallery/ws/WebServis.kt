package org.foi.rampu.geogallery.ws

import android.util.Log
import org.foi.rampu.geogallery.GalleryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WebServis(val activity : GalleryActivity) {

    private val ws = WsLocationInfo.locationInfoService

    fun loadLocationInfo() {
        ws.getLocationInfo().enqueue(
            object : Callback<WsResponse>
            {
                override fun onResponse(call: Call<WsResponse>?, response: Response<WsResponse>?)
                {
                    if (response?.isSuccessful == true)
                    {
                        val responseBody = response.body()
                        //since pages is a map, get the only key from set of keys to access extract with it from the map "pages
                        var mapKey = responseBody.query?.pages?.keys?.elementAt(0)
                        Log.i("INFO KEY", mapKey.toString())

                        var info = responseBody.query?.pages?.get(mapKey)?.extract

                        if (info == null) info = "Nije dohvaceno"
                        Log.i("INFO", info)

                    }
                    else
                    {
                        //displayWebServiceErrorMessage()
                        Log.i("ERR", "Greska on Response")
                    }
                }
                override fun onFailure(call: Call<WsResponse>?, t: Throwable?)
                {
                    //displayWebServiceErrorMessage()
                    Log.i("ERR", "Greska on Failure")
                }
            }
        )
    }

    fun prepareLocationInfo()
    {

    }

    fun displayLocationInfo(info : String)
    {
        Log.i("INFO", info)
    }
}