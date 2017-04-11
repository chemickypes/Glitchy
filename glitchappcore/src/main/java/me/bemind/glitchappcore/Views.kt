package me.bemind.glitchappcore

import android.graphics.Bitmap

/**
 * Created by angelomoroni on 04/04/17.
 */

interface IImageView {
    fun setImagebitmap(bitmap : Bitmap)
    fun showGetImageError(t : Throwable)

    fun showSaveLoader()
    fun onSavedImage()
    fun onSaveImageError(t:Throwable)

    fun updateState(state: State)

}

class NullImageView : IImageView {
    override fun setImagebitmap(bitmap: Bitmap) {
    }

    override fun showSaveLoader() {
    }

    override fun onSavedImage() {
    }

    override fun onSaveImageError(t: Throwable) {
    }

    override fun showGetImageError(t: Throwable) {
    }

    override fun updateState(state: State) {

    }
}