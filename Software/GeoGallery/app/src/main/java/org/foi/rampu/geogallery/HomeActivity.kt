package org.foi.rampu.geogallery

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.toColor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.foi.rampu.geogallery.classes.FolderManager
import org.foi.rampu.geogallery.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityHomeBinding

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
        for (i in 0..9)
        {
            folderManager.createFolderIcon(mockLocations[i])
        }

    }

}