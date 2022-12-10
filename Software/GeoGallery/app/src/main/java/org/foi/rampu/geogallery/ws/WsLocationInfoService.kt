package org.foi.rampu.geogallery.ws

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WsLocationInfoService { //titles=Zagreb
    @GET("api.php?action=query&prop=extracts&exlimit=1&explaintext=1&exsectionformat=plain&format=json")
    fun getLocationInfo(@retrofit2.http.Query("titles") location : String) : Call<WsResponse>
}