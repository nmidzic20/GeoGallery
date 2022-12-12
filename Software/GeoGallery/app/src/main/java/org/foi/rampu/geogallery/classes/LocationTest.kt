package org.foi.rampu.geogallery.classes

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import org.foi.rampu.geogallery.HomeActivity
import java.util.*

class LocationTest(val activity: HomeActivity){

    lateinit var locationRequest: LocationRequest
    var country = ""
    var city = ""

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


    fun checkGPS() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

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
                    }catch(sendIntentExcepton : IntentSender.SendIntentException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                }
            }
        }
    }

    fun CountryName(fusedLocationProviderClient: FusedLocationProviderClient) : String {
        if(checkLocationPermission()) {
            val task = fusedLocationProviderClient.lastLocation
            task.addOnCompleteListener {
                if (it != null) {
                    val geoCoder = Geocoder(activity, Locale.getDefault())
                    val adress = geoCoder.getFromLocation(task.result.latitude, task.result.longitude, 1)
                    country = adress[0].countryName
                }
            }
        }
        return country
    }

    fun CityName(fusedLocationProviderClient: FusedLocationProviderClient) : String {
        if(checkLocationPermission()) {
            val task = fusedLocationProviderClient.lastLocation
            task.addOnCompleteListener {
                if(task != null) {
                    val geoCoder = Geocoder(activity, Locale.getDefault())
                    val adress = geoCoder.getFromLocation(task.result.latitude, task.result.longitude, 1)
                    city = adress[0].locality
                }
            }
        }
        return city
    }

}