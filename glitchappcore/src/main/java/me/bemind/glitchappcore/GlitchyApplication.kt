package me.bemind.glitchappcore

import android.app.Application
import android.util.Log
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import java.io.File

/**
 * Created by angelomoroni on 10/04/17.
 */

open class GlitchyCoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RxPaparazzo.register(this).withFileProviderPath("Glitchy/saved/")
        Log.d(getString(R.string.app_name),"Create")
    }
}