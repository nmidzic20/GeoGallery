package org.foi.rampu.geogallery

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.foi.rampu.geogallery.databinding.ActivityGoogleMapsBinding

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback { //OnMapReadyCallback je interface

    lateinit var viewBinding: ActivityGoogleMapsBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val locationPermissionCode = 2
    private lateinit var currentLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        viewBinding = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation = locationResult!!.locations[0]
                PripremiMapu() //Pozovi funkciju za pripremu mape tek kada dobijes lokaciju,
                                // odnosno koordinate, ne prije!
            }
        }

        startLocationUpdates()
    }

    private fun PripremiMapu() {
        val mapFragment = supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment? //SupportMapFragment klasa pridodana mapi u xmlu
                                                                                                    // (Inicijaliziramo ju ovdje i pridodajemo mapfragment-u
                                                                                                    //Ovi ? su potrebni jer se prilikom klika natrag crasha
                                                                                                    // aplikacija jer ne može pronaći SupportMapFragment
        if(mapFragment != null)
            mapFragment.getMapAsync(this) //Javi kada je mapa spremna za upotrebu, poziva se
                                                // onMapReady funkcija koja je implementirana u sučelju OnMapReadyCallback
    }

    override fun onMapReady(googleMap: GoogleMap){
        googleMap?.clear() //Pri svakom pozivu obriši stari marker

        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude) //Reprezentira latitude i longitude te ih sprema kao stupnjeve
        val markerOptions = MarkerOptions().position(latLng).title("Current Location") //Inicijaliziram marker za mapu

        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng)) //Animirano fokusiraj kameru na lokaciju
        googleMap?.addMarker(markerOptions) //Dodaj oznaku na trenutnu lokaciju
    }

    private fun startLocationUpdates() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode)
        }
        else {
            val locationRequest = LocationRequest()
            locationRequest.interval = 2000
            locationRequest.fastestInterval = 1000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
}