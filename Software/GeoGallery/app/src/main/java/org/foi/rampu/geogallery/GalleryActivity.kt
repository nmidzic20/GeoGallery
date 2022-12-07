package org.foi.rampu.geogallery

import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.foi.rampu.geogallery.classes.PhotoGallery
import org.foi.rampu.geogallery.classes.VideoGallery
import org.foi.rampu.geogallery.databinding.ActivityGalleryBinding


class GalleryActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityGalleryBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val photoGallery = PhotoGallery(this)
        val videoGallery = VideoGallery(this)

        photoGallery.display_photos()
        videoGallery.display_videos()
    }


}