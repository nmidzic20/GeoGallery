package org.foi.rampu.geogallery.classes

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.foi.rampu.geogallery.GalleryActivity
import org.foi.rampu.geogallery.fragments.GalleryFragment


class PhotoGallery(val galleryFragment: GalleryFragment, private var context: Context) {

    fun display_photos()
    {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection : String? = null
        val selectionArgs = arrayOf<String>()
        val sortOrder : String? = null
        var mediaLocationManager : MediaLocationManager = MediaLocationManager()

        //fetch all images from MediaStore
        galleryFragment.activity?.applicationContext?.contentResolver?.query(
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

                //var locationMetadata = mediaLocationManager.getLocationMetadata(galleryFragment, imgUri)

                var locationMetadata = mediaLocationManager.getLocationFromMediaName(imgUri,
                    this.galleryFragment.requireActivity()
                )

                //display image only if its location metadata matches folder location name
                Log.i("IMAGE_SHOWN?", locationMetadata.toString() + " " + galleryFragment.folderName)

                if (locationMetadata.street == galleryFragment.folderName)
                {
                    createImageView(imgUri)
                    Log.i("IMAGE_SHOWN?", "yes")
                }
                else
                    Log.i("IMAGE_SHOWN?", "no")

            }
        }
    }

    fun createImageView(imgUri : Uri)
    {

        val layout = galleryFragment.view?.findViewById<View>(org.foi.rampu.geogallery.R.id.gridLayout) as ViewGroup
            //line below only for activities, if using findViewById for fragments, need to get it from view/getView() first!
            //activity.findViewById<View>(org.foi.rampu.geogallery.R.id.gridLayout) as ViewGroup

        val imageView = ImageView(galleryFragment.activity)
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

            //display image full size using android default gallery image viewer
            galleryFragment.activity?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    imgUri
                )
            )
        }

        imageView.setOnLongClickListener {
            Toast.makeText(context, "Long click detected", Toast.LENGTH_SHORT).show()
            true
        }

        layout.addView(imageView)
        Log.i("imgview", imageView.toString())

        setImageMargins(imageView)

    }

    fun setImageMargins(imageView : ImageView)
    {
        val param = imageView.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        imageView.layoutParams = param
    }

}