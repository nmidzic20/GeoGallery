package org.foi.rampu.geogallery.classes

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import org.foi.rampu.geogallery.HomeActivity

class Location(val activity: HomeActivity, fusedLocationProviderClient: FusedLocationProviderClient){
    lateinit var locationRequest: LocationRequest

    fun checkLocationPermission() {
        if(ActivityCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            checkGPS()
        }else{
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    fun checkGPS() {
        locationRequest = LocationRequest.create()
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

                //getUserLocation()

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
}