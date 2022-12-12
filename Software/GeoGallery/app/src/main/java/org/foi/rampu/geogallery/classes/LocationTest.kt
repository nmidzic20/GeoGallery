package org.foi.rampu.geogallery.classes

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import org.foi.rampu.geogallery.HomeActivity
import java.util.*
import javax.security.auth.callback.Callback

class LocationTest(val activity: HomeActivity){

    private lateinit var locationRequest: LocationRequest
    private var country = ""
    private var city = ""
    private var street = ""

    fun checkLocationPermission() : Boolean{
        if(ActivityCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            checkGPS()
            return true
        }else{
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            checkGPS()
            return false
        }
    }

    private fun checkGPS() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 1000

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(activity.applicationContext).checkLocationSettings(builder.build())

        result.addOnCompleteListener{ task ->
            try{
                val response = task.getResult(
                    ApiException::class.java
                )
            }catch(e : ApiException){
                e.printStackTrace()
                when(e.statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try{
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(activity,200)
                    }catch(sendIntentException : IntentSender.SendIntentException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                }
            }
        }
    }

    fun countryName(fusedLocationProviderClient: FusedLocationProviderClient) : String {
            if(checkLocationPermission()) {
                val task = fusedLocationProviderClient.lastLocation
                task.addOnCompleteListener {
                    if (it != null && task.result != null) {
                        val geoCoder = Geocoder(activity, Locale.getDefault())
                        val address =
                            geoCoder.getFromLocation(task.result.latitude, task.result.longitude, 1)
                        country = address[0].countryName
                        Log.i("ADDRESS", (address + " " + country).toString())
                        object : Callback {}.run {
                            Log.i("ADDRESS CALLBACK", country)
                            activity.locationInfo.value = mutableMapOf(
                                "country" to country,
                                "city" to activity.locationInfo.value?.get("city").toString(),
                                "street" to activity.locationInfo.value?.get("street").toString()
                            )
                        }
                    }
                }
            }
        return country
    }

    fun cityName(fusedLocationProviderClient: FusedLocationProviderClient) : String {
            if(checkLocationPermission()) {
                val task = fusedLocationProviderClient.lastLocation
                task.addOnCompleteListener {
                    if (it != null && task?.result != null) {
                        val geoCoder = Geocoder(activity, Locale.getDefault())
                        val address =
                            geoCoder.getFromLocation(task.result.latitude, task.result.longitude, 1)
                        city = if (address[0].locality != null)
                            address[0].locality
                        else
                            address[0].subAdminArea
                        object : Callback {}.run {
                            Log.i("ADDRESS CALLBACK", city)
                            activity.locationInfo.value = mutableMapOf(
                                "country" to activity.locationInfo.value?.get("country").toString(),
                                "city" to city,
                                "street" to activity.locationInfo.value?.get("street").toString()
                            )
                        }
                    }
                }
            }
        return city
    }
    fun streetName(fusedLocationProviderClient: FusedLocationProviderClient) : String{
        if(checkLocationPermission()) {
            val task = fusedLocationProviderClient.lastLocation
            task.addOnCompleteListener {
                if (it != null && task?.result != null) {
                    val geoCoder = Geocoder(activity, Locale.getDefault())
                    val address =
                        geoCoder.getFromLocation(task.result.latitude, task.result.longitude, 1)
                    street = if (address[0].thoroughfare != null)
                        address[0].thoroughfare
                    else
                        ""
                    object : Callback {}.run {
                        Log.i("ADDRESS CALLBACK", street)
                        activity.locationInfo.value = mutableMapOf(
                            "country" to activity.locationInfo.value?.get("country").toString(),
                            "city" to activity.locationInfo.value?.get("city").toString(),
                            "street" to street
                        )
                    }
                }
            }
        }
        return street
    }
}
