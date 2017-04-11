package me.bemind.glitchappcore

import android.graphics.Bitmap

/**
 * Created by angelomoroni on 10/04/17.
 */

data class Response<out T, out V>(val activity: T, val image: V)

data class Image(val bitmap: Bitmap,val effect: Effect,val saved:Boolean)

enum class State {
    BASE,EFFECT
}

enum class Effect {
    BASE,ANAGLYPH, GLITCH
}