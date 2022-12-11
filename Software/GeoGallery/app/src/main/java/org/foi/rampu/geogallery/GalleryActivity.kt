package org.foi.rampu.geogallery

import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.foi.rampu.geogallery.adapters.GalleryPagerAdapter
import org.foi.rampu.geogallery.classes.PhotoGallery
import org.foi.rampu.geogallery.classes.VideoGallery
import org.foi.rampu.geogallery.ws.LocationInfoManager
import org.foi.rampu.geogallery.databinding.ActivityGalleryBinding
import org.foi.rampu.geogallery.fragments.GalleryFragment
import org.foi.rampu.geogallery.fragments.LocationInfoFragment


class GalleryActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityGalleryBinding

    lateinit var tabLayout: TabLayout
    lateinit var viewPager2: ViewPager2

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        tabLayout = findViewById(R.id.tabs)
        viewPager2 = findViewById(R.id.viewpager)
        viewPager2.adapter = GalleryPagerAdapter(supportFragmentManager, lifecycle)

        val galleryPagerAdapter = GalleryPagerAdapter(supportFragmentManager, lifecycle)

        galleryPagerAdapter.addFragment(
            GalleryPagerAdapter.FragmentItem(
                R.string.gallery,
                GalleryFragment::class
            )
        )
        galleryPagerAdapter.addFragment(
            GalleryPagerAdapter.FragmentItem(
                R.string.location_info,
                LocationInfoFragment::class
            )
        )

        viewPager2.adapter = galleryPagerAdapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(galleryPagerAdapter.fragmentItems[position].titleRes)
        }.attach()

        /*val photoGallery = PhotoGallery(this)
        val videoGallery = VideoGallery(this)
        val locationInfoManager = LocationInfoManager(this)

        photoGallery.display_photos()
        videoGallery.display_videos()

        val folderName = intent.getStringExtra("FOLDER_NAME")

        locationInfoManager.loadLocationInfo(folderName!!)
        */
    }


}