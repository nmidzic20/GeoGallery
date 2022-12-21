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

    val location = LocationTest(this) //Inicijalizacija LocationTest klase

    //Mapa koja će se prilikom callbacka iz klase Location test popunit vrijednostima
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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) //Inicijalizacija providera za lokaciju

        addressResultReceiver = LocationAddressResultReceiver(Handler()) //Receiver za dobivenu adresu

        locationCallback = object : LocationCallback(){ //Callback za lokaciju koji će prilikom dobivene lokacije biti pozvan
            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation = locationResult.locations[0]
                viewBinding.tvLocation.text = location.countryName(currentLocation)+ "," + location.cityName(currentLocation)+ "," + location.streetName(currentLocation)
            }
        }

        startLocationUpdates() //Funkcija za konstantno dobivanje lokacije (Non stop ažuriranje lokacije korisnika)
        location.checkLocationPermission() //Prilikom pokretanja provjeri dopuštenja za lokaciju

        viewBinding.ibtnGoogleMaps.setOnClickListener{
            val intent = Intent(this, GoogleMapsActivity::class.java)
            startActivity(intent)
        }

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
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //Dohvati logiranog korisnika
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut() //Odlogiraj korisnika kako bi bili sigurni da će prilikom
                                        // sljedećeg pokretanja biti potreban login

            Firebase.auth.signOut() //Odlogiraj nas sa firebasea
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
            //Provjeri dopuštenja za lokaciju
        }
        else {
            //Pokreni lokaciju
            val locationRequest = LocationRequest()
            locationRequest.interval = 2000
            locationRequest.fastestInterval = 1000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //Provjeri pristpni kod sa pristupnim kodom lokacije i ako su dobivena dopuštenja provjeri dopuštenja za lokaciju
        // i pokreni dobivanje lokacije

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
        //Ako se aplikacija ugasi i ponovo pokrene provjeri dopuštenja za lokaciju
        // i pokreni dobivanje lokacije

        super.onResume()
        startLocationUpdates()
        location.checkLocationPermission()
    }
}
