package org.foi.rampu.geogallery

import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import org.foi.rampu.geogallery.databinding.ActivityGalleryBinding


class GalleryActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityGalleryBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        display_photos()
        display_videos()
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun display_videos()
    {
        val projection = arrayOf(MediaStore.Video.Media._ID)
        val selection : String? = null
        val selectionArgs = arrayOf<String>()
        val sortOrder : String? = null

        applicationContext.contentResolver.query(
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

                val thumbnail = applicationContext.contentResolver.loadThumbnail(videoUri, Size(500, 500), null)

                createVideoView(videoUri, thumbnail)


            }
        }

    }

    fun createVideoView(videoUri : Uri, thumbnail : Bitmap)
    {
        val layout = findViewById<View>(org.foi.rampu.geogallery.R.id.gridLayout) as ViewGroup
        val videoView = VideoView(this)
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

        val mediaController = MediaController(this)
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
        val frameLayout = FrameLayout(this)
        frameLayout.layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            )

        val ivThumbnail = ImageView(this)
        ivThumbnail.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        ivThumbnail.layoutParams.height = 500
        ivThumbnail.layoutParams.width = 500
        ivThumbnail.scaleType = ImageView.ScaleType.FIT_XY
        ivThumbnail.setImageBitmap(thumbnail)

        val playIcon = ImageView(this)
        playIcon.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        playIcon.layoutParams.height = 200
        playIcon.layoutParams.width = 200
        playIcon.scaleType = ImageView.ScaleType.FIT_XY
        playIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_arrow))

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

        setImageMargins(imageView)

    }

    fun setImageMargins(imageView : ImageView)
    {
        val param = imageView.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        imageView.layoutParams = param
    }

}