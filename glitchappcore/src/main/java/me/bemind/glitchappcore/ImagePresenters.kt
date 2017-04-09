package me.bemind.glitchappcore

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle

/**
 * Created by angelomoroni on 04/04/17.
 */


interface IImagePresenter {
    fun openImage()

    fun saveImage()

    fun glitchImage()

    fun saveInstanceState(outState: Bundle?)

    fun restoreInstanceState(savedInstanceState: Bundle?)

    fun subscribe(view: IImageView)

    fun unsubscribe()
}

class ImagePresenter (val context: Context) : IImagePresenter{

    lateinit var bitmap:Bitmap
    val BITMAP_K = "bitmap_k"

    var imageView: IImageView = NullImageView()


    override fun openImage() {

    }

    override fun saveImage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun glitchImage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribe(view: IImageView) {
        imageView = view
    }

    override fun unsubscribe() {
        imageView = NullImageView()
    }

    override fun saveInstanceState(outState: Bundle?) {
        outState?.putParcelable(BITMAP_K,bitmap);
    }

    override fun restoreInstanceState(savedInstanceState: Bundle?) {
        if(savedInstanceState!=null)bitmap = savedInstanceState.getParcelable(BITMAP_K)
    }
}
