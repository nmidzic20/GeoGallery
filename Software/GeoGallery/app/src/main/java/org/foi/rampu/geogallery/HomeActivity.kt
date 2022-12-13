package org.foi.rampu.geogallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.foi.rampu.geogallery.classes.AllLocationsInfo
import org.foi.rampu.geogallery.classes.FolderManager
import org.foi.rampu.geogallery.classes.SavedLocationInfo
import org.foi.rampu.geogallery.databinding.ActivityHomeBinding
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString


class HomeActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /*var locationInfo : MutableLiveData<MutableMap<String, String>> = MutableLiveData(
        mutableMapOf(
            "country" to "",
            "city" to "",
            "street" to ""
        )
    )*/

    companion object {
        val realLocations = mutableListOf<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

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

        //doesn't work
        AllLocationsInfo.savedLocationInfo.forEach {
            if (it.city != "") realLocations.add(it.city)
        }
        Log.i("FOLDER", realLocations.toString())

        var size = AllLocationsInfo.savedLocationInfo.size
        Log.i("FOLDER SIZE", size.toString())
        //


        val prefs = getSharedPreferences("locations_preferences", Context.MODE_PRIVATE)
        //prefs.edit().remove("all_locations_media_taken").commit();

        var locsString = prefs.getString("all_locations_media_taken", "No locations saved yet")
        Log.i("ADDRESS HOME ACTIVITY ", locsString.toString())

        var obj = mutableListOf<SavedLocationInfo>()
        if (locsString != "No locations saved yet")
            obj = Json.decodeFromString<MutableList<SavedLocationInfo>>(locsString!!)

        Log.i("ADDRESS HOME ACTIVITY ", obj.toString())

        //size = locsList!!.size
        //Log.i("ADDRESS HOME ACTIVITY SIZE", size.toString())


        /*
        if (size != 0)
        {
            for (i in 0..size)
            {
                Log.i("ADDRESS FOLDER CITY")
                if (AllLocationsInfo.savedLocationInfo[i].city != "")
                    folderManager.createFolderIcon(AllLocationsInfo.savedLocationInfo[i].city)
            }
        }
        */
    }
}