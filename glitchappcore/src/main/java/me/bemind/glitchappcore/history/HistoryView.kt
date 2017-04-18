package me.bemind.glitchappcore.history

import android.graphics.Bitmap
import android.os.Bundle
import me.bemind.glitchappcore.GlitchyBaseActivity

/**
 * Created by angelomoroni on 14/04/17.
 */

interface IHistoryView {

    var hasHistory : Boolean
    fun back() : Boolean
    fun setPreviousImage(back: Bitmap?)

    fun saveInstanceState(glitchyBaseActivity: GlitchyBaseActivity, outState: Bundle?)
    fun restoreSavedInstanceState(glitchyBaseActivity: GlitchyBaseActivity, savedInstanceState: Bundle?)

}