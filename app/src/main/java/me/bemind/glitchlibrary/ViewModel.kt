package me.bemind.glitchlibrary

import android.support.annotation.IdRes
import me.bemind.glitch.Effect

/**
 * Created by angelomoroni on 24/04/17.
 */

data class EffectView(val effect: Effect,val name:String, @IdRes val layout:Int )