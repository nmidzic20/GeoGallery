package org.foi.rampu.geogallery

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.marginBottom
import org.foi.rampu.geogallery.databinding.ActivityGalleryBinding

class GalleryActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        display_photos()
    }

    fun display_photos()
    {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection : String? = null
        val selectionArgs = arrayOf<String>()
        val sortOrder : String? = null

        applicationContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val id = cursor.getLong(idColumn)
                var contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val imgUri = Uri.parse(contentUri.toString())
                Log.i("URI", imgUri.toString())
                createImageView(imgUri)

            }
        }
    }

    fun createImageView(imgUri : Uri)
    {
        val layout = findViewById<View>(org.foi.rampu.geogallery.R.id.gridLayout) as ViewGroup
        val imageView = ImageView(this)
        imageView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )


        imageView.setImageURI(null)
        imageView.setImageURI(imgUri)
        imageView.layoutParams.height = 500
        imageView.layoutParams.width = 500
        imageView.scaleType = ImageView.ScaleType.FIT_XY

        imageView.setOnClickListener {
            Toast.makeText(this, "photo, location info", Toast.LENGTH_SHORT).show()
        }

        layout.addView(imageView)
        Log.i("imgview", imageView.toString())

        val param = imageView.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        imageView.layoutParams = param


    }

}