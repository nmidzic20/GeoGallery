package org.foi.rampu.geogallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.foi.rampu.geogallery.databinding.ActivityHomeBinding
import android.os.ResultReceiver
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.foi.rampu.geogallery.classes.*


class HomeActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    val location = LocationTest(this) //Inicijalizacija LocationTest klase

    private lateinit var locationCallback: LocationCallback
    private val locationPermissionCode = 2
    private lateinit var addressResultReceiver: LocationAddressResultReceiver
    private lateinit var currentLocation: Location


    lateinit var navDrawerLayout: DrawerLayout
    lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //navigation drawer
        navDrawerLayout = viewBinding.drawerLayout
        navView = viewBinding.navView


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) //Inicijalizacija providera za lokaciju

        addressResultReceiver = LocationAddressResultReceiver(Handler()) //Receiver za dobivenu adresu

        locationCallback = object : LocationCallback(){ //Callback za lokaciju koji će prilikom dobivene lokacije biti pozvan

            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation = locationResult.locations[0]

                val country = location.countryName(currentLocation)
                val city = location.cityName(currentLocation)
                val street = location.streetName(currentLocation)

                viewBinding.tvLocation.text = country+ "," + city+ "," + street
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


        //navigation drawer
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.title) {
                "Statistics" -> Statistics.showDialog(this)
            //Toast.makeText(this, "Prozor", Toast.LENGTH_SHORT).show()
            }
            navDrawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }

        val folderManager = FolderManager(this)


        val prefs = getSharedPreferences("locations_preferences", Context.MODE_PRIVATE)
        //prefs.edit().remove("all_locations_media_taken").commit();

        var locsString = prefs.getString("all_locations_media_taken", resources.getString(R.string.shared_prefs_default_location_info))
        Log.i("HOME ACTIVITY ", locsString.toString())

        var locations = mutableSetOf<SavedLocationInfo>()
        if (locsString != resources.getString(R.string.shared_prefs_default_location_info))
            locations = Json.decodeFromString<MutableSet<SavedLocationInfo>>(locsString!!)

        Log.i("HOME DESERIALISED ", locations.toString())

        if (locations != null) AllLocationsInfo.savedLocationInfo = locations!!


        var size = AllLocationsInfo.savedLocationInfo.size
        Log.i("HOME ACTIVITY SIZE", size.toString())

        if (size != 0)
        {
            folderManager.createFolderIconsCountries()
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
