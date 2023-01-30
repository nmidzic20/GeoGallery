package org.foi.rampu.geogallery.classes

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isInvisible
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.fragments.GalleryFragment

class VideoGallery(private val galleryFragment: GalleryFragment, private var context: Context) {

    @RequiresApi(Build.VERSION_CODES.R)
    fun displayVideos()
    {
        val projection = arrayOf(MediaStore.Video.Media._ID)
        val selection : String? = null
        val selectionArgs = arrayOf<String>()
        val sortOrder : String? = null
        val mediaLocationManager = MediaLocationManager()

        galleryFragment.activity?.applicationContext?.contentResolver?.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val id = cursor.getLong(idColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val videoUri = Uri.parse(contentUri.toString())
                Log.i("URI", videoUri.toString())


                val locationMetadata = mediaLocationManager.getLocationFromMediaName(videoUri,
                    this.galleryFragment.requireActivity()
                )

                //display video only if its location metadata matches folder location name
                Log.i("VIDEO_SHOWN?", locationMetadata.toString() + " " + galleryFragment.folderName)

                if (locationMetadata.street == galleryFragment.folderName)
                {
                    val thumbnail = galleryFragment.requireActivity().applicationContext.contentResolver.loadThumbnail(videoUri, Size(500, 500), null)

                    createVideoView(videoUri, thumbnail)
                    Log.i("VIDEO_SHOWN?", "yes")
                }
                else
                    Log.i("VIDEO_SHOWN?", "no")


            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createVideoView(videoUri : Uri, thumbnail : Bitmap)
    {
        val layout = galleryFragment.view?.findViewById<View>(R.id.gridLayout) as ViewGroup
        val videoView = VideoView(galleryFragment.activity)
        videoView.layoutParams = createLayoutParams()

        videoView.setVideoURI(null)
        videoView.setVideoURI(videoUri)
        videoView.layoutParams.height = 500
        videoView.layoutParams.width = 500
        videoView.isInvisible = true

        Log.i("videoview", videoView.toString())

        val mediaController = MediaController(galleryFragment.activity)
        videoView.setMediaController(mediaController)

        //surround image view & video view with framelayout, image view is for thumbnail
        //when user clicks play, image view -> invisible, video view -> visible & opposite for stop
        //changed logic to - go to full screen when playing, so no need to hide thumbnail
        createThumbnail(thumbnail, videoView, layout, videoUri)


    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createThumbnail(thumbnail: Bitmap, videoView: VideoView, layout: ViewGroup, videoUri: Uri)
    {
        val frameLayout = galleryFragment.context?.let { FrameLayout(it) }
        frameLayout?.layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            )

        val ivThumbnail = createThumbnailImageView(thumbnail)
        val playIcon = createPlayIconImageView()
        val playIconWhiteBackground = createPlayIconWhiteBackground()

        centerPlayIcon(frameLayout!!, playIcon, playIconWhiteBackground)

        frameLayout.addView(ivThumbnail)
        frameLayout.addView(playIconWhiteBackground)
        frameLayout.addView(playIcon)
        frameLayout.addView(videoView)
        layout.addView(frameLayout)

        setVideoMargins(frameLayout)

        setPlayOrPauseLogic(ivThumbnail, videoView, videoUri)

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setPlayOrPauseLogic(ivThumbnail: ImageView, videoView: VideoView, videoUri: Uri)
    {
        ivThumbnail.setOnClickListener {

            //go to full screen to play
            galleryFragment.activity?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    videoUri
                )
            )
            videoView.start()
        }

        ivThumbnail.setOnLongClickListener {
            showPopup(ivThumbnail, videoUri)
            true
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showPopup(view: View, videoUri: Uri) {

        val uriList = mutableListOf(videoUri)
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.popup_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.share -> {
                    shareVideo(videoUri)
                    //Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                }
                R.id.delete -> {
                    deleteVideo(uriList)
                    //Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                }
            }

            true
        }

        popup.show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun deleteVideo(uriList: List<Uri>) {

        val activity = context as Activity
        val req = MediaStore.createDeleteRequest(context.contentResolver, uriList)

        activity.startIntentSenderForResult(req.intentSender, 123,
            null, 0, 0, 0, null
        )

        //activity.finish()
        //activity.startActivity(activity.intent)
    }

    private fun shareVideo(uri: Uri) {

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "video/mp4"
            putExtra(Intent.EXTRA_TEXT, "Snimljeno u GeoGalleryju!")
            type = "text/plain"
        }

        (context as Activity).startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun setVideoMargins(frameLayout : FrameLayout)
    {
        val param = frameLayout.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        frameLayout.layoutParams = param
    }

    private fun createThumbnailImageView(thumbnail : Bitmap) : ImageView
    {
        val ivThumbnail = ImageView(galleryFragment.activity)
        ivThumbnail.layoutParams = createLayoutParams()
        ivThumbnail.layoutParams.height = 500
        ivThumbnail.layoutParams.width = 500
        ivThumbnail.scaleType = ImageView.ScaleType.FIT_XY
        ivThumbnail.setImageBitmap(thumbnail)
        return ivThumbnail
    }

    private fun createPlayIconImageView() : ImageView
    {
        val playIcon = ImageView(galleryFragment.activity)
        playIcon.layoutParams = createLayoutParams()
        playIcon.layoutParams.height = 600
        playIcon.layoutParams.width = 600
        playIcon.scaleType = ImageView.ScaleType.FIT_XY
        //playIcon.scaleType=ImageView.ScaleType.FIT_CENTER
        playIcon.setImageDrawable(galleryFragment.activity?.resources?.getDrawable(R.drawable.ic_baseline_play_button))
        return playIcon
    }

    private fun createPlayIconWhiteBackground() : ImageView
    {
        val playIconWhiteBackground = ImageView(galleryFragment.activity)
        playIconWhiteBackground.layoutParams = createLayoutParams()
        playIconWhiteBackground.setImageDrawable(galleryFragment.activity?.resources?.getDrawable(R.drawable.ic_baseline_play_arrow_24))
        return playIconWhiteBackground
    }

    private fun createLayoutParams() : ViewGroup.LayoutParams
    {
        return ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    private fun centerPlayIcon(frameLayout: FrameLayout, playIcon: ImageView, playIconWhiteBackground : ImageView)
    {
        val param = frameLayout.layoutParams as FrameLayout.LayoutParams
        param.gravity = Gravity.CENTER
        playIcon.layoutParams = param
        playIconWhiteBackground.layoutParams = param
    }
}