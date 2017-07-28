package me.bemind.glitchlibrary

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import me.bemind.glitchappcore.asString
import java.util.*

/**
 * Created by angelomoroni on 28/07/17.
 */

object GlitchyEventTracker {
    private const val EFFECT_EVENT: String = "effect_name"
    private const val SAVE_EVENT: String = "save_event"
    private const val SHARE_EVENT: String = "share_event"

    private var mFirebaseAnalytics : FirebaseAnalytics? = null

    fun init(context:Context) : GlitchyEventTracker{
        if(mFirebaseAnalytics==null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        }
        return this
    }

    fun trackEffectClick(effectName:String) : GlitchyEventTracker{
        val b = getBaseBundle()
        b.putString(FirebaseAnalytics.Param.ITEM_NAME,effectName)

        return trackEvent(EFFECT_EVENT,b)
    }

    fun trackSaveImage() : GlitchyEventTracker {
        return trackEvent(SAVE_EVENT, getBaseBundle())
    }

    fun trackShareImage() : GlitchyEventTracker {
        return trackEvent(SHARE_EVENT, getBaseBundle())
    }

    fun trackEvent(event:String,b: Bundle) : GlitchyEventTracker{
        mFirebaseAnalytics?.logEvent(event,b)
        return this
    }

    private fun getBaseBundle() : Bundle {
        val b = Bundle()
        b.putString("timestamp", Date().asString())
        return b
    }
}