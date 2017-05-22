package me.bemind.glitchlibrary

import com.facebook.appevents.AppEventsLogger
import me.bemind.glitchappcore.GlitchyCoreApplication

/**
 * Created by angelomoroni on 10/04/17.
 */

class GlitchyApplication : GlitchyCoreApplication() {

    override fun onCreate() {
        super.onCreate()
        AppEventsLogger.activateApp(this)
    }
}
