package org.foi.rampu.geogallery.ws

import retrofit2.Call
import retrofit2.http.GET

interface WsLocationInfoService {
    @GET("api.php?action=query&prop=extracts&exlimit=1&titles=Zagreb&explaintext=1&exsectionformat=plain&format=json")
    fun getLocationInfo() : Call<WsResponse>
}