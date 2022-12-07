package org.foi.rampu.geogallery

import android.R.attr.button
import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import org.foi.rampu.geogallery.classes.MediaGallery
import org.foi.rampu.geogallery.databinding.ActivityGalleryBinding


class GalleryActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityGalleryBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val mediaGallery = MediaGallery(this)

        mediaGallery.display_photos()
        mediaGallery.display_videos()
    }


}