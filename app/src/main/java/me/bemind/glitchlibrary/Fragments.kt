package me.bemind.glitchlibrary

import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jraf.android.alibglitch.GlitchEffect

/**
 * Created by angelomoroni on 03/05/17.
 */

class MenuFragment: Fragment() {

    var rootview : View? = null

    val logoApp : ImageView? by lazy {
        rootview?.findViewById(R.id.app_logo) as ImageView
    }

    val versionApp : TextView by lazy {
        rootview?.findViewById(R.id.version_text) as TextView
    }

    val bemindLink:TextView by lazy {
        rootview?.findViewById(R.id.bemind_link) as TextView
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootview = inflater?.inflate(R.layout.menu_fragment,container)!!

        logoApp?.setOnClickListener {
            GlitchEffect.showGlitch(activity)
        }

        val pInfo = activity.packageManager.getPackageInfo(activity.packageName,0)
        versionApp.apply {
            text = getString(R.string.v_d,pInfo.versionName)
        }

        bemindLink.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("http://www.bemind.me")
            startActivity(i)
        }

        return rootview!!
    }



}
