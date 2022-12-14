package org.foi.rampu.geogallery.classes

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import org.foi.rampu.geogallery.CameraActivity
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

    fun countryName(currentLocation: Location) : String {
                        val geoCoder = Geocoder(activity, Locale.getDefault())
                        val address = geoCoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
                        country = address[0].countryName
                        Log.i("ADDRESS", (address + " " + country).toString())
                        object : Callback {}.run {
                            Log.i("ADDRESS CALLBACK", country)
                            

                }
        return country
    }

    fun cityName(currentLocation: Location) : String {
                        val geoCoder = Geocoder(activity, Locale.getDefault())
                        val address = geoCoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
                        city = if (address[0].locality != null)
                                address[0].locality
                            else if (address[0].subAdminArea != null)
                                address[0].subAdminArea
                            else
                                ""
                        object : Callback {}.run {
                            Log.i("ADDRESS CALLBACK", city)


                }
        return city
    }
    fun streetName(currentLocation: Location) : String{
                    val geoCoder = Geocoder(activity, Locale.getDefault())
                    val address = geoCoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
                    street = if (address[0].thoroughfare != null)
                            address[0].thoroughfare
                        else
                            ""
                    object : Callback {}.run {
                        Log.i("ADDRESS CALLBACK", street)

            }
        return street
    }
}
