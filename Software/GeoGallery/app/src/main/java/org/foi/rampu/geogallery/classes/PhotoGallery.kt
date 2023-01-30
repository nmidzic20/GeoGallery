package org.foi.rampu.geogallery.classes

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.fragments.GalleryFragment


class PhotoGallery(private val galleryFragment: GalleryFragment, private var context: Context) {

    @RequiresApi(Build.VERSION_CODES.R)
    fun displayPhotos()
    {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection : String? = null
        val selectionArgs = arrayOf<String>()
        val sortOrder : String? = null
        val mediaLocationManager = MediaLocationManager()

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
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val imgUri = Uri.parse(contentUri.toString())
                Log.i("URI", imgUri.toString())

                //var locationMetadata = mediaLocationManager.getLocationMetadata(galleryFragment, imgUri)

                val locationMetadata = mediaLocationManager.getLocationFromMediaName(imgUri,
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

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createImageView(imgUri : Uri)
    {

        val layout = galleryFragment.view?.findViewById<View>(R.id.gridLayout) as ViewGroup
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
            //Toast.makeText(context, "Long click detected", Toast.LENGTH_SHORT).show()
            showPopup(imageView, imgUri)
            true
        }

        layout.addView(imageView)

        setImageMargins(imageView)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showPopup(view: View, imgUri: Uri) {

        val uriList = mutableListOf(imgUri)
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.popup_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.share -> {
                    sharePhoto(imgUri)
                    //Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                }
                R.id.delete -> {
                    deletePhoto(uriList)
                    // Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                }
            }

            true
        }

        popup.show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun deletePhoto(uriList: List<Uri>) {

        val activity = context as Activity
        val req = MediaStore.createDeleteRequest(context.contentResolver, uriList)

        activity.startIntentSenderForResult(req.intentSender, 123,
            null, 0, 0, 0, null
        )

        //activity.finish()
        //activity.startActivity(activity.intent)
    }

    private fun sharePhoto(uri: Uri) {

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpeg"
            putExtra(Intent.EXTRA_TEXT, "Slikano u GeoGalleryju!")
            type = "text/plain"
        }

        (context as Activity).startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun setImageMargins(imageView : ImageView)
    {
        val param = imageView.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        imageView.layoutParams = param
    }

}