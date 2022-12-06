package org.foi.rampu.geogallery.classes

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.foi.rampu.geogallery.HomeActivity


class MediaGallery() : HomeActivity() {

    init {
        createFoldersHomeActivity()
    }

    fun createFoldersHomeActivity()
    {
        val layout = findViewById<View>(org.foi.rampu.geogallery.R.id.constraintLayout) as ViewGroup
        val tv = TextView(this)
        tv.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tv.text = "Added tv"
        layout.addView(tv)
    }
}