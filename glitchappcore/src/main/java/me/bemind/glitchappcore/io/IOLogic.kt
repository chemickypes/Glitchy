package me.bemind.glitchappcore.io

import android.graphics.Bitmap

/**
 * Created by angelomoroni on 18/04/17.
 */

interface IIOLogic {

    fun openImage()

    fun saveImage(bitmap: Bitmap)
}
