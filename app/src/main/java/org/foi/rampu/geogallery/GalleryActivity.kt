package org.foi.rampu.geogallery

import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.foi.rampu.geogallery.adapters.GalleryPagerAdapter
import org.foi.rampu.geogallery.classes.PhotoGallery
import org.foi.rampu.geogallery.classes.Statistics
import org.foi.rampu.geogallery.classes.VideoGallery
import org.foi.rampu.geogallery.ws.LocationInfoManager
import org.foi.rampu.geogallery.databinding.ActivityGalleryBinding
import org.foi.rampu.geogallery.fragments.GalleryFragment
import org.foi.rampu.geogallery.fragments.LocationInfoFragment


class GalleryActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityGalleryBinding

    lateinit var tabLayout: TabLayout
    lateinit var viewPager2: ViewPager2

    lateinit var navDrawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        viewBinding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.title_bar_layout)

        //navigation drawer
        navDrawerLayout = viewBinding.drawerLayout
        navView = viewBinding.navView

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

        //navigation drawer
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.title) {
                "Statistics" -> Statistics.showDialog(this)
                //Toast.makeText(this, "Prozor", Toast.LENGTH_SHORT).show()
            }
            navDrawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }

    }


}