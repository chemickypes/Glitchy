package me.bemind.glitchappcore

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import com.github.oliveiradev.lib.RxPhoto
import com.github.oliveiradev.lib.shared.TypeRequest

/**
 * Created by angelomoroni on 04/04/17.
 */


interface IImagePresenter {
    fun openImage(typeRequest: TypeRequest = TypeRequest.GALLERY, w:Int = 1024, h:Int = 1024)
    fun openImageFromGallery(w:Int = 1024, h:Int = 1024)

    fun openImageFromCamera(w:Int = 1024, h:Int = 1024)

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

    val imageLogic : IImageLogic = ImageLogic()

    var imageView: IImageView = NullImageView()


    override fun openImage(typeRequest: TypeRequest , w:Int , h:Int ) {
        imageLogic.getImage(context,typeRequest,w,h)
                .doOnNext {
                    b -> imageView.setImagebitmap(b)
                }
                .doOnError {
                    t -> imageView.showGetImageError(t)
                }
                .subscribe()
    }

    override fun openImageFromGallery(w: Int, h: Int) {
        openImage(TypeRequest.GALLERY,w,h)
    }

    override fun openImageFromCamera(w: Int, h: Int) {
        openImage(TypeRequest.CAMERA,w,h)
    }

    override fun saveImage() {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun glitchImage() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
