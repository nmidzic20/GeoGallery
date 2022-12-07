package org.foi.rampu.geogallery.classes

import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isInvisible
import org.foi.rampu.geogallery.GalleryActivity
import org.foi.rampu.geogallery.HomeActivity
import org.foi.rampu.geogallery.R


class MediaGallery(val activity: GalleryActivity) {

    fun display_photos()
    {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection : String? = null
        val selectionArgs = arrayOf<String>()
        val sortOrder : String? = null

        activity.applicationContext.contentResolver.query(
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun display_videos()
    {
        val projection = arrayOf(MediaStore.Video.Media._ID)
        val selection : String? = null
        val selectionArgs = arrayOf<String>()
        val sortOrder : String? = null

        activity.applicationContext.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val id = cursor.getLong(idColumn)
                var contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val videoUri = Uri.parse(contentUri.toString())
                Log.i("URI", videoUri.toString())

                val thumbnail = activity.applicationContext.contentResolver.loadThumbnail(videoUri, Size(500, 500), null)

                createVideoView(videoUri, thumbnail)


            }
        }

    }

    fun createVideoView(videoUri : Uri, thumbnail : Bitmap)
    {
        val layout = activity.findViewById<View>(org.foi.rampu.geogallery.R.id.gridLayout) as ViewGroup
        val videoView = VideoView(activity)
        videoView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

        videoView.setVideoURI(null)
        videoView.setVideoURI(videoUri)
        videoView.layoutParams.height = 500
        videoView.layoutParams.width = 500
        videoView.isInvisible = true
        //videoView.scal = ImageView.ScaleType.FIT_XY FIX VIDEO SIZE

        Log.i("videoview", videoView.toString())

        val mediaController = MediaController(activity)
        videoView.setMediaController(mediaController)

        //surround image view & video view with framelayout, image view is for thumbnail
        //when user clicks play, image view -> invisible, video view -> visible & opposite for stop
        createThumbnail(thumbnail, videoView, layout)

        //videoView.start();
        //imageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(urlPath, MediaStore.Video.Thumbnails.MICRO_KIND))
        //bitmap = ThumbnailUtils.createVideoThumbnail(getRealPathFromURI(videoUri), MediaStore.Images.Thumbnails.MINI_KIND);

    }

    fun createThumbnail(thumbnail: Bitmap, videoView: VideoView, layout: ViewGroup)
    {
        val frameLayout = FrameLayout(activity)
        frameLayout.layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            )

        val ivThumbnail = ImageView(activity)
        ivThumbnail.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        ivThumbnail.layoutParams.height = 500
        ivThumbnail.layoutParams.width = 500
        ivThumbnail.scaleType = ImageView.ScaleType.FIT_XY
        ivThumbnail.setImageBitmap(thumbnail)

        val playIcon = ImageView(activity)
        playIcon.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        playIcon.layoutParams.height = 200
        playIcon.layoutParams.width = 200
        playIcon.scaleType = ImageView.ScaleType.FIT_XY
        playIcon.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_baseline_play_arrow))

        /*to make video play in full size of video view
        val relativeLayout = RelativeLayout(this)
        relativeLayout.layoutParams =
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
            )

        relativeLayout.addView(videoView)
        val relLay = videoView.layoutParams as RelativeLayout.LayoutParams
        relLay.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        relLay.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        relLay.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        relLay.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        videoView.layoutParams = relLay //causes layout update*/

        frameLayout.addView(ivThumbnail)
        frameLayout.addView(playIcon)
        frameLayout.addView(videoView)
        layout.addView(frameLayout)

        setVideoMargins(frameLayout)

        setPlayOrPauseLogic(playIcon, videoView, ivThumbnail)

    }

    fun setPlayOrPauseLogic(playIcon : ImageView, videoView : VideoView, ivThumbnail : ImageView)
    {
        ivThumbnail.setOnClickListener {

            //click will register only if ivThumbnail visible
            videoView.isInvisible = false
            playIcon.isInvisible = true
            ivThumbnail.isInvisible = true
            videoView.start()
        }

        videoView.setOnCompletionListener {
            playIcon.isInvisible = false
            ivThumbnail.isInvisible = false
            videoView.isInvisible = true
        }
    }

    fun setVideoMargins(frameLayout : FrameLayout)
    {
        val param = frameLayout.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        frameLayout.layoutParams = param
    }

    fun createImageView(imgUri : Uri)
    {
        val layout = activity.findViewById<View>(org.foi.rampu.geogallery.R.id.gridLayout) as ViewGroup
        val imageView = ImageView(activity)
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
            Toast.makeText(activity, "photo, location info", Toast.LENGTH_SHORT).show()
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