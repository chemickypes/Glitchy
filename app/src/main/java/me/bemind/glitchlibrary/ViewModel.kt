package me.bemind.glitchlibrary

import android.support.annotation.IdRes
import me.bemind.glitch.Effect
import java.util.ArrayList

/**
 * Created by angelomoroni on 24/04/17.
 */

data class EffectView(val effect: Effect, @IdRes val name:Int, @IdRes val layout:Int )

object EffectList{
    val data = ArrayList<EffectView>()

    init {
        data.add(EffectView(Effect.GLITCH,R.string.glitch,R.layout.effect_glitch_layout))
        data.add(EffectView(Effect.WEBP,R.string.webp,R.layout.effect_glitch_layout))
        data.add(EffectView(Effect.ANAGLYPH,R.string.anaglyph,R.layout.effect_anaglyph_layout))
        data.add(EffectView(Effect.SWAP,R.string.swap,R.layout.effect_glitch_layout))
        data.add(EffectView(Effect.GHOST,R.string.ghost,R.layout.effect_ghost_layout))
        data.add(EffectView(Effect.WOBBLE,R.string.wobble,R.layout.effect_ghost_layout))
//        data.add(EffectView(Effect.NOISE,R.string.noise,R.layout.effect_glitch_layout))
        data.add(EffectView(Effect.NOISE,R.string.noise,R.layout.effect_anaglyph_layout))
        //data.add(EffectView(Effect.HOOLOOVOO,R.string.hooloovoo,R.layout.effect_hooloovoo_layout))
    }
}