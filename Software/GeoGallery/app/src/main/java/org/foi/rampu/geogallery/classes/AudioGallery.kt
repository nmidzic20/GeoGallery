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
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.fragments.GalleryFragment


class AudioGallery(private val galleryFragment: GalleryFragment, private var context: Context) {

    @RequiresApi(Build.VERSION_CODES.R)
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
                Log.i("AUDIO", "uri$audioUri")

                var name = mediaLocationManager.getFileName(audioUri, this.galleryFragment.requireActivity())
                if (name?.get(0)  == '{')
                {
                    val locationMetadata = mediaLocationManager.getLocationFromMediaName(audioUri,
                        this.galleryFragment.requireActivity()
                    )

                    if (locationMetadata.street == galleryFragment.folderName)
                    {
                        createAudioView(audioUri)
                        Log.i("AUDIO", "yes")
                    }
                    else
                        Log.i("AUDIO", "no")
                }


               /* val locationMetadata = mediaLocationManager.getLocationFromMediaName(audioUri,
                    this.galleryFragment.requireActivity()
                )

                if (locationMetadata.street == galleryFragment.folderName)
                {
                    createAudioView(audioUri)
                    Log.i("AUDIO", "yes")
                }
                else
                    Log.i("AUDIO", "no")*/
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
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

        imageView.setOnLongClickListener {
            //Toast.makeText(context, "Long click detected", Toast.LENGTH_SHORT).show()
            showPopup(imageView, audioUri)
            true
        }

        layout.addView(imageView)
        Log.i("imgview", imageView.toString())

        setImageMargins(imageView)

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showPopup(view: ImageView, audioUri: Uri) {

        val uriList = mutableListOf(audioUri)
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.popup_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.share -> {
                    shareAudio(audioUri)
                    //Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                }
                R.id.delete -> {
                    deleteAudio(uriList, view)
                    // Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                }
            }

            true
        }

        popup.show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun deleteAudio(uriList: List<Uri>, view : ImageView) {

        val activity = context as Activity
        val req = MediaStore.createDeleteRequest(context.contentResolver, uriList)

        activity.startIntentSenderForResult(req.intentSender, 123,
            null, 0, 0, 0, null
        )

        //view.isVisible = false
        //activity.finish()
        //activity.startActivity(activity.intent)
    }

    private fun shareAudio(uri: Uri) {

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpeg"
            putExtra(Intent.EXTRA_TEXT, "Snimljeno u GeoGalleryju!")
            type = "text/plain"
        }

        (context as Activity).startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun setImageMargins(imageView : ImageView) {

        val param = imageView.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        imageView.layoutParams = param
    }

}