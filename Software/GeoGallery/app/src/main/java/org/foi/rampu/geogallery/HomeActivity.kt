package org.foi.rampu.geogallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.foi.rampu.geogallery.classes.FolderManager
import org.foi.rampu.geogallery.classes.LocationTest
import org.foi.rampu.geogallery.databinding.ActivityHomeBinding
import android.os.ResultReceiver
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    val location = LocationTest(this)

    var locationInfo : MutableLiveData<MutableMap<String, String>> = MutableLiveData(
        mutableMapOf(
            "country" to "",
            "city" to "",
            "street" to ""
        )
    )

    private lateinit var locationCallback: LocationCallback
    private val locationPermissionCode = 2
    private lateinit var addressResultReceiver: LocationAddressResultReceiver
    private lateinit var currentLocation: Location


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        addressResultReceiver = LocationAddressResultReceiver(Handler())
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation = locationResult.locations[0]
                viewBinding.tvLocation.text = location.countryName(currentLocation)+ "," + location.cityName(currentLocation)+ "," + location.streetName(currentLocation)
            }
        }

        startLocationUpdates()
        location.checkLocationPermission()

        viewBinding.ibtnLocation.setOnClickListener{
            location.checkLocationPermission()
        }

        viewBinding.ibtnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        viewBinding.ibtnAudio.setOnClickListener {
            val intent = Intent(this, AudioActivity::class.java)
            startActivity(intent)
        }

        viewBinding.btnLogout.setOnClickListener{
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()

            Firebase.auth.signOut()
            startActivity(Intent(applicationContext, MainActivity::class.java))

        }

        val folderManager = FolderManager(this)

        val mockLocations = listOf<String>("Zagreb", "Varaždin", "Rijeka", "Graz", "Rome", "Dubrovnik",
            "Trieste", "Venice", "Osijek", "Pula")
        for (i in 0..9)
        {
            folderManager.createFolderIcon(mockLocations[i])
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                location.checkLocationPermission()
                startLocationUpdates()
            }
        }
    }

    private inner class LocationAddressResultReceiver(handler: Handler) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        }
    }
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        location.checkLocationPermission()
    }
}
