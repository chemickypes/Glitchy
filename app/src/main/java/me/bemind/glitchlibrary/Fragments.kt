package me.bemind.glitchlibrary

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.jraf.android.alibglitch.GlitchEffect

/**
 * Created by angelomoroni on 03/05/17.
 */

class MenuFragment: Fragment() {

    var rootview : View? = null

    val logoApp : ImageView? by lazy {
        rootview?.findViewById(R.id.app_logo) as ImageView
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootview = inflater?.inflate(R.layout.menu_fragment,container)!!

        logoApp?.setOnClickListener {
            GlitchEffect.showGlitch(activity)
        }
        return rootview!!
    }



}
