package me.bemind.glitchlibrary

import com.facebook.appevents.AppEventsLogger
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import me.bemind.glitchappcore.GlitchyCoreApplication

/**
 * Created by angelomoroni on 10/04/17.
 */

class GlitchyApplication : GlitchyCoreApplication() {

    override fun onCreate() {
        super.onCreate()
        AppEventsLogger.activateApp(this)
        SharedPreferencesManager.init(this, true)
    }
}
