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
import org.foi.rampu.geogallery.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityHomeBinding

    enum class ColourType(val color: Int) {
        RED(R.color.red),
        BLUE(R.color.blue),
        YELLOW(R.color.yellow),
        GREEN(R.color.green),
        ORANGE(R.color.orange),
        PURPLE(R.color.purple_200);

        fun toColour(context: Context) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                context.resources.getColor(color, null)
            else
                context.resources.getColor(color)
    }

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

        for (i in 0..10)
        {
            createFolderIcon(i)
        }

    }


    fun createFolderIcon(index : Int)
    {
        val layout = findViewById<View>(org.foi.rampu.geogallery.R.id.gridLayout) as ViewGroup
        val ivFolder = ImageView(this)
        ivFolder.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                )

        ivFolder.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_folder))

        colourFolder(ivFolder, index)

        ivFolder.layoutParams.height = 200
        ivFolder.layoutParams.width = 200

        ivFolder.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }

        layout.addView(ivFolder)

    }

    fun colourFolder(ivFolder : ImageView, i : Int)
    {
        var index = i % ColourType.values().size

        var colorId = ColourType.values()[index].color
        val color = resources.getColor(colorId)

        ivFolder.setColorFilter(color)
    }
}