package me.bemind.glitchappcore

import android.graphics.Bitmap

/**
 * Created by angelomoroni on 10/04/17.
 */

data class Response<out T, out V>(val activity: T, val image: V)
