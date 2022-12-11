package org.foi.rampu.geogallery.fragments

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Photo
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.annotation.RequiresApi
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.classes.PhotoGallery
import org.foi.rampu.geogallery.classes.VideoGallery

class GalleryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val photoGallery = PhotoGallery(this)
        val videoGallery = VideoGallery(this)

        photoGallery?.display_photos()
        videoGallery?.display_videos()

    }
}