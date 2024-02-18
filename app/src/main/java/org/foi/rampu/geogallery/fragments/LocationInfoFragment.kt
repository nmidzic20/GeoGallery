package org.foi.rampu.geogallery.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.classes.PhotoGallery
import org.foi.rampu.geogallery.ws.LocationInfoManager

class LocationInfoFragment : Fragment() {

    private lateinit var loadingCircle: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val locationInfoManager = LocationInfoManager(this)

        val folderName = activity?.intent?.getStringExtra("FOLDER_NAME")?.split("_")?.get(1)
        Log.i("FOLDER", folderName!!)

        locationInfoManager.loadLocationInfo(folderName!!)

    }
}