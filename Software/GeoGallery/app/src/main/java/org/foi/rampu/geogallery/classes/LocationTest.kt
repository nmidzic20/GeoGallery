package org.foi.rampu.geogallery.classes

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
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

    //Provjeri permissions za lokaciju u oba slucaja provjeri GPS kako bi bili sigurni da je upaljen
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

        locationRequest = LocationRequest() //Postavke za lokaciju
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 1000

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest) //Posalji zahtjev za lokaciju

        builder.setAlwaysShow(true)

        //Provjeri dobivenu lokaciju
        val result = LocationServices.getSettingsClient(activity.applicationContext).checkLocationSettings(builder.build())

        result.addOnCompleteListener{ task ->
            try{
                //Kada je GPS upaljen
                val response = task.getResult(
                    ApiException::class.java
                )
            }catch(e : ApiException){
                //Kada je GPS ugašen
                e.printStackTrace()
                when(e.statusCode){
                    //Šalji zahtjev za paljenje GPS-a
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try{
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(activity,200)
                    }catch(sendIntentException : IntentSender.SendIntentException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        //Kada su postavke nedosutpne
                    }
                }
            }
        }
    }

    fun countryName(currentLocation: Location) : String {
        //Provjeri vraća li Geocoder kakve informacije kako bi spriječili rušenje aplikacije ili vraćanje praznog string-a
        //prilikom stalnog dobivanja lokacije. Inicijalizacija Geocodera te zatim dodavanje geocoderu latitude i longitudude od trenune lokacije kako bi
        // mogli dobiti povratnu informaciju  države, grada i ulice. U country se sprema string "dešifrirane" lokacije povezan sa državom.
        // Kako bi se spriječio povratak praznog stringa napravila se callback funkcija koja tek kada dobije vrijednost spremi tu vrijednost u mapu.
        ///U ovom slučaju sprema se država, a ostale vrijednosti ako postoje samo se ponovo prepisuju kako bi spriječili da ako već postoje da ih
        // slučajno ne izbrišemo. Na kraju vraća se naziv države.

        if(Geocoder(activity,Locale.getDefault()) != null) {
            val geoCoder = Geocoder(activity, Locale.getDefault())
            val address = geoCoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
            country = address[0].countryName
            Log.i("ADDRESS", (address + " " + country).toString())
            object : Callback {}.run {
                Log.i("ADDRESS CALLBACK", country)
                CurrentLocationInfo.locationInfo.value = mutableMapOf(
                    "country" to country,
                    "city" to CurrentLocationInfo.locationInfo.value?.get("city").toString(),
                    "street" to CurrentLocationInfo.locationInfo.value?.get("street").toString()
                )
                Log.i("ADDRESS CALLBACK", country)
            }
        }

        return country
    }

    fun cityName(currentLocation: Location) : String {
        //Provjeri vraća li Geocoder kakve informacije kako bi spriječili rušenje aplikacije ili vraćanje praznog string-a
        //prilikom stalnog dobivanja lokacije. Inicijalizacija Geocodera te zatim dodavanje geocoderu latitude i longitudude od trenune lokacije kako bi
        // mogli dobiti povratnu informaciju  države, grada i ulice. U city se sprema string "dešifrirane" lokacije povezan sa gradom. Prije samog callbacka
        //kako bi program radio brže stavljen je if uvjet koji ispituje više slučajeva prije nego vrati prazan string.
        // Kako bi se spriječio povratak praznog stringa napravila se callback funkcija koja tek kada dobije vrijednost spremi tu vrijednost u mapu.
        ///U ovom slučaju sprema se grad, a ostale vrijednosti ako postoje samo se ponovo prepisuju kako bi spriječili da ako već postoje da ih
        // slučajno ne izbrišemo. Na kraju vraća se naziv grada.

        if(Geocoder(activity,Locale.getDefault()) != null) {
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
                CurrentLocationInfo.locationInfo.value = mutableMapOf(
                    "country" to CurrentLocationInfo.locationInfo.value?.get("country").toString(),
                    "city" to city,
                    "street" to CurrentLocationInfo.locationInfo.value?.get("street").toString()
                )
                Log.i("ADDRESS CALLBACK", city)
            }
        }

        return city
    }

    fun streetName(currentLocation: Location) : String{
        //Provjeri vraća li Geocoder kakve informacije kako bi spriječili rušenje aplikacije ili vraćanje praznog string-a
        //prilikom stalnog dobivanja lokacije. Inicijalizacija Geocodera te zatim dodavanje geocoderu latitude i longitudude od trenune lokacije kako bi
        // mogli dobiti povratnu informaciju  države, grada i ulice. U street se sprema string "dešifrirane" lokacije povezan sa ulicom. Prije samog callbacka
        //kako bi program radio brže stavljen je if uvjet koji ispituje slučaj prije nego vrati prazan string.
        // Kako bi se spriječio povratak praznog stringa napravila se callback funkcija koja tek kada dobije vrijednost spremi tu vrijednost u mapu.
        ///U ovom slučaju sprema se ulica, a ostale vrijednosti ako postoje samo se ponovo prepisuju kako bi spriječili da ako već postoje da ih
        // slučajno ne izbrišemo. Na kraju vraća se naziv ulice.

        if(Geocoder(activity,Locale.getDefault()) != null) {
            val geoCoder = Geocoder(activity, Locale.getDefault())
            val address =
                geoCoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
            street = if (address[0].thoroughfare != null)
                address[0].thoroughfare
            else
                ""
            object : Callback {}.run {
                Log.i("ADDRESS CALLBACK", street)
                CurrentLocationInfo.locationInfo.value = mutableMapOf(
                    "country" to CurrentLocationInfo.locationInfo.value?.get("country").toString(),
                    "city" to CurrentLocationInfo.locationInfo.value?.get("city").toString(),
                    "street" to street
                )
                Log.i("ADDRESS CALLBACK", street)
                
            }
        }
        return street
    }
}
