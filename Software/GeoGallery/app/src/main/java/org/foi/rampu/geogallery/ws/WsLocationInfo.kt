package org.foi.rampu.geogallery.ws

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WsLocationInfo {
    const val BASE_URL = "https://hr.wikipedia.org/w/"
    private var instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val locationInfoService = instance.create(WsLocationInfoService::class.java)
}