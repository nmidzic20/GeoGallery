package org.foi.rampu.geogallery

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.foi.rampu.geogallery.classes.FolderManager
import org.foi.rampu.geogallery.classes.Location
import org.foi.rampu.geogallery.databinding.ActivityHomeBinding
import java.io.IOException
import java.util.*

class HomeActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityHomeBinding

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val location = Location(this,fusedLocationProviderClient)

        viewBinding.getPosition.setOnClickListener {
            location.checkLocationPermission()
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
        for (i in 0..10)
        {
            folderManager.createFolderIcon("Lokacija")
        }

    }

    /*
    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->

            val location = task.getResult()

            if(location != null){
                try{
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val address = geocoder.getFromLocation(location.latitude,location.longitude,1)

                    val address_line = address[0].getAddressLine(0)
                    viewBinding.tvLocation.text=address_line


                } catch(e: IOException){

                }
            }
        }
    }*/
}