package org.foi.rampu.geogallery

import android.content.ContentUris
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.foi.rampu.geogallery.databinding.ActivityHomeBinding


open class HomeActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.ibtnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
        viewBinding.btnLogout.setOnClickListener{
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()

            Firebase.auth.signOut()
            startActivity(Intent(applicationContext, MainActivity::class.java))

        }

        for (i in 1..10)
            createFolderIcon()

    }


    fun createFolderIcon()
    {
        val layout = findViewById<View>(org.foi.rampu.geogallery.R.id.gridLayout) as ViewGroup
        val ivFolder = ImageView(this)
        ivFolder.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                )

        ivFolder.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_folder))
        ivFolder.setColorFilter(R.color.blue)
        ivFolder.layoutParams.height = 200
        ivFolder.layoutParams.width = 200

        ivFolder.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }

        layout.addView(ivFolder)

    }
}