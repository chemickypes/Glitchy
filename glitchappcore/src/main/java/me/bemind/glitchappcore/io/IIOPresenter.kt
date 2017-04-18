package me.bemind.glitchappcore.io

import android.graphics.Bitmap

/**
 * Created by angelomoroni on 18/04/17.
 */

interface IIOView{
    fun setImage(bitmap:Bitmap)
    fun showErrorGetImage(t:Throwable)
    fun showSuccessSaveImage(fileName:String)
    fun showErrorSaveImage(t: Throwable)
}

interface IIOPresenter {
    fun openImage()
    fun saveImage()
}
