package org.foi.rampu.geogallery.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.classes.AudioGallery
import org.foi.rampu.geogallery.classes.PhotoGallery
import org.foi.rampu.geogallery.classes.VideoGallery

class GalleryFragment : Fragment() {

    var folderName : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        folderName = activity?.intent?.getStringExtra("FOLDER_NAME")?.split("_")?.get(0)

        val photoGallery = PhotoGallery(this, requireContext())
        val videoGallery = VideoGallery(this, requireContext())
        val audioGallery = AudioGallery(this, requireContext())

        photoGallery.displayPhotos()
        videoGallery.displayVideos()
        audioGallery.displayAudio()
    }
}