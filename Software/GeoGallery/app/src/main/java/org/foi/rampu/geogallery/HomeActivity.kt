package org.foi.rampu.geogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val ibtnCamera = findViewById<ImageButton>(R.id.ibtnCamera)
        ibtnCamera.setOnClickListener{

        }
    }
}