package org.foi.rampu.geogallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.foi.rampu.geogallery.classes.FolderManager
import org.foi.rampu.geogallery.classes.LocationTest
import org.foi.rampu.geogallery.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var locationInfo : MutableLiveData<MutableMap<String, String>> = MutableLiveData(
        mutableMapOf(
            "country" to "",
            "city" to "",
            "street" to ""
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val location = LocationTest(this)

        locationInfo.observe(this, Observer {
            Log.i("ADDRESS LOCATION INFO MUTABLE DATA", it.toString())
            viewBinding.tvLocation.text = it["country"] + "," + it["city"] +  "," + it["street"]
            }
        )

        viewBinding.getPosition.setOnClickListener {
            location.countryName(fusedLocationProviderClient)
            location.cityName(fusedLocationProviderClient)
            location.streetName(fusedLocationProviderClient)
        }

        viewBinding.ibtnLocation.setOnClickListener{
            location.checkLocationPermission()
        }

        viewBinding.ibtnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
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
}