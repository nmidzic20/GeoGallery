package org.foi.rampu.geogallery.classes

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.fragments.GalleryFragment


class AudioGallery(private val galleryFragment: GalleryFragment) {

    fun displayAudio()
    {
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection : String? = null
        val selectionArgs = arrayOf<String>()
        val sortOrder : String? = null
        val mediaLocationManager = MediaLocationManager()

        //fetch all audio files from MediaStore
        galleryFragment.activity?.applicationContext?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val id = cursor.getLong(idColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val audioUri = Uri.parse(contentUri.toString())
                Log.i("URI", audioUri.toString())

                val locationMetadata = mediaLocationManager.getLocationFromMediaName(audioUri,
                    this.galleryFragment.requireActivity()
                )

                if (locationMetadata.street == galleryFragment.folderName)
                {
                    createAudioView(audioUri)
                    Log.i("IMAGE_SHOWN?", "yes")
                }
                else
                    Log.i("IMAGE_SHOWN?", "no")
            }
        }
    }

    private fun createAudioView(audioUri : Uri) {

        val layout = galleryFragment.view?.findViewById<View>(R.id.gridLayout) as ViewGroup

        val imageView = ImageView(galleryFragment.activity)
        imageView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

        imageView.setImageURI(null)
        imageView.layoutParams.height = 500
        imageView.layoutParams.width = 500
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.setImageResource(R.drawable.ic_voice)

        imageView.setOnClickListener {

            //display image full size using android default gallery image viewer
            galleryFragment.activity?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    audioUri
                )
            )
        }

        layout.addView(imageView)
        Log.i("imgview", imageView.toString())

        setImageMargins(imageView)

    }

    private fun setImageMargins(imageView : ImageView) {

        val param = imageView.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        imageView.layoutParams = param
    }

}