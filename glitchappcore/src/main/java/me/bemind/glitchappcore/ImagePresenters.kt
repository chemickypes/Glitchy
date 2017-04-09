package me.bemind.glitchappcore

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import com.github.oliveiradev.lib.RxPhoto
import com.github.oliveiradev.lib.shared.TypeRequest
import rx.Observable
import rx.Subscription

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

    fun onBackPressed() :Boolean
}

class ImagePresenter (val context: Context) : IImagePresenter{
    val BITMAP_K = "bitmap_k"

    val imageLogic : IImageLogic = ImageLogic()

    var imageView: IImageView = NullImageView()

    var subscriber : Subscription? = null


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
       subscriber = imageLogic.glitchImage(
               imageLogic.firstBitmap())
                .filter { b -> b!=null }
                .flatMap { b -> Observable.just(b)  }
                .doOnNext { b -> imageView.setImagebitmap(b!!) }
                .doOnError { t -> imageView.showGetImageError(t) }
                .subscribe()
    }

    override fun subscribe(view: IImageView) {
        imageView = view

        if (imageLogic.hasHistory()) imageView.setImagebitmap(imageLogic.lastBitmap()!!)
    }

    override fun unsubscribe() {
        imageView = NullImageView()
        subscriber?.unsubscribe()
    }

    override fun saveInstanceState(outState: Bundle?) {

        if (imageLogic.hasHistory()) outState?.putParcelableArrayList(BITMAP_K,
                imageLogic.getStack())

    }

    override fun restoreInstanceState(savedInstanceState: Bundle?) {
        if(savedInstanceState!=null){
             imageLogic.setStack(savedInstanceState.getParcelableArrayList(BITMAP_K))
        }
    }

    override fun onBackPressed(): Boolean {
        if(imageLogic.canBack()){
            imageView.setImagebitmap(imageLogic.back()!!)
            return false
        }else{
            return true
        }
    }
}
