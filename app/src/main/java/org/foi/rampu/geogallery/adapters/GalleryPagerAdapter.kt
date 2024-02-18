package org.foi.rampu.geogallery.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.foi.rampu.geogallery.R
import org.foi.rampu.geogallery.fragments.GalleryFragment
import org.foi.rampu.geogallery.fragments.LocationInfoFragment
import kotlin.reflect.KClass

class GalleryPagerAdapter(fragmentManager : FragmentManager, lifecycle : Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    data class FragmentItem(val titleRes: Int, val fragmentClass: KClass<*>)

    val fragmentItems = ArrayList<FragmentItem>()

    fun addFragment(fragment: FragmentItem) {
        fragmentItems.add(fragment)
    }

    override fun getItemCount(): Int = fragmentItems.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GalleryFragment()
            else -> LocationInfoFragment()
        }
    }
}