package me.bemind.glitchappcore

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Created by angelomoroni on 04/04/17.
 */



interface IImagePresenter {

    var modState : State

    fun openImage(activity: Activity, file:File) : Bitmap?

    fun openImageFromGallery(activity:GlitchyBaseActivity, w: Int = 1024, h:Int = 1024)

    fun openImageFromCamera(activity:GlitchyBaseActivity, w: Int = 1024, h:Int = 1024)

    fun saveImage()

    fun saveEffect()

    fun glitchImage(effect: Effect, progress: Int = 20)

    fun saveInstanceState(outState: Bundle?)

    fun restoreInstanceState(savedInstanceState: Bundle?)

    fun subscribe(view: IImageView)

    fun unsubscribe()

    fun onBackPressed() :Boolean

    fun getIImageLogic() :IImageLogic


}

class ImagePresenter (val context: Context) : IImagePresenter{

    override var modState: State = State.BASE
//        get() = modState
        set(value) {
            field = value
            imageView.updateState(field)
        }


    private val TAG: String? = "IMAGE GLITCHER"

    private val BITMAP_K = "bitmap_k"
    private val STATE_K = "state_k"


    val imageLogic : IImageLogic = ImageLogic()

    var imageView: IImageView = NullImageView()

    var disposable : Disposable? = null




    override fun getIImageLogic(): IImageLogic {
        return imageLogic
    }

    override fun openImage( activity: Activity,file: File) : Bitmap? {

        return imageLogic.getImage(activity,file)
    }

    override fun openImageFromGallery(activity: GlitchyBaseActivity,w:Int,h:Int) {
       handleObservable( RxPaparazzo.single(activity)
               .usingGallery(),w,h)
    }



    override fun openImageFromCamera(activity: GlitchyBaseActivity,w:Int,h:Int) {
        handleObservable(RxPaparazzo.single(activity)
                .usingCamera(),w,h)

    }

    private fun handleObservable(observable: Observable<com.miguelbcr.ui.rx_paparazzo2.entities.Response<GlitchyBaseActivity, FileData?>>,
                                 w:Int,h:Int) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { response ->
                    if (response.resultCode() != Activity.RESULT_OK) {
                        //nothing
                        Observable.empty<Response<GlitchyBaseActivity,Bitmap>>()
                    }else {
                        val b = (response.targetUI() as GlitchyBaseActivity).getImagePresenter().getIImageLogic()
                                .getImage(response.targetUI(),response.data()!!.file,w,h)
                        Observable.just(Response((response.targetUI() as GlitchyBaseActivity),b))
                    }
                }
                .doOnNext { b -> b.activity.setImagebitmap(b.image); }
                .doOnError { t -> t.printStackTrace() }
                .doOnComplete { Log.i(TAG,"Load complete") }
                .subscribe()
    }

    override fun saveImage() {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun glitchImage(effect: Effect,progress: Int) {

       disposable = when(effect){
           Effect.GLITCH -> glitchEffect()
           Effect.ANAGLYPH -> anaglyphEffect(progress)
           else -> Observable.empty<Bitmap?>().subscribe()
       }
    }

    override fun subscribe(view: IImageView) {
        imageView = view

        if (imageLogic.hasHistory()) imageView.setImagebitmap(imageLogic.lastBitmap()!!)
    }

    override fun unsubscribe() {
        imageView = NullImageView()
        disposable?.dispose()
    }

    override fun saveInstanceState(outState: Bundle?) {

        outState?.putSerializable(STATE_K,modState)
        if (imageLogic.hasHistory()){
            outState?.putParcelableArrayList(BITMAP_K,
                    imageLogic.getStack())
        }

    }

    override fun saveEffect() {
        imageLogic.saveEffect()
    }

    override fun restoreInstanceState(savedInstanceState: Bundle?) {
        if(savedInstanceState?.containsKey(BITMAP_K)?:false){
             imageLogic.setStack(savedInstanceState?.getParcelableArrayList(BITMAP_K))
        }


        if(savedInstanceState?.containsKey(STATE_K)?:false) modState = savedInstanceState?.getSerializable(STATE_K) as State
    }

    override fun onBackPressed(): Boolean {
        if(imageLogic.canBack()){
            imageView.setImagebitmap(imageLogic.back()!!)
            return false
        }else{
            return true
        }
    }

    fun anaglyphEffect(progress: Int = 20) : Disposable{
        return imageLogic.anaglyphImage(progress)
                .filter { b -> b!=null }
                .flatMap { b -> Observable.just(b) }
                .subscribe({
                    b -> imageView.setImagebitmap(b!!)
                        },
                        {
                            t -> imageView.showGetImageError(t)
                        }
                )
    }

    fun glitchEffect() : Disposable{
        return imageLogic.glitchImage(
                imageLogic.firstBitmap())
                .filter { b -> b!=null }
                .flatMap { b -> Observable.just(b)  }
                .subscribe(
                        {
                            b -> imageView.setImagebitmap(b!!)
                        },
                        {
                            t -> imageView.showGetImageError(t)
                        }
                )
    }
}
