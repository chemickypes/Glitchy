package me.bemind.glitchappcore.io

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import com.miguelbcr.ui.rx_paparazzo2.entities.Response
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.bemind.glitchappcore.GlitchyBaseActivity

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

    var BASE_DIM : Int

    var ioLogic : IIOLogic

    enum class TypePick {
        CAMERA,GALLERY
    }
    fun openImage(activity: GlitchyBaseActivity,typePick: TypePick,w:Int = BASE_DIM,h:Int = BASE_DIM)
    fun saveImage()
}

class IOPresenter : IIOPresenter {


    override var BASE_DIM: Int = 1024
    set(value) {
        /*nothing*/
    }

    override var ioLogic: IIOLogic = IOLogic()
    set(value) {/*nothing to assign */}

    private val TAG = "IOPResenter"

    var ioView : IIOView? = null


    override fun openImage(activity: GlitchyBaseActivity,typePick: IIOPresenter.TypePick,w:Int,h:Int) {
        when (typePick){
            IIOPresenter.TypePick.CAMERA -> openImageFromCamera(activity,w,h)
            else -> openImageFromGallery(activity, w, h)
        }
    }

    override fun saveImage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun openImageFromGallery(activity: GlitchyBaseActivity,w:Int,h:Int) {
        handleObservable( RxPaparazzo.single(activity).usingGallery(),w,h)
    }



    private fun openImageFromCamera(activity: GlitchyBaseActivity,w:Int,h:Int) {
        handleObservable(RxPaparazzo.single(activity)
                .usingCamera(),w,h)

    }


    private fun handleObservable(observable: Observable<Response<GlitchyBaseActivity, FileData?>>,
                                 w:Int, h:Int) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { response ->
                    if (response.resultCode() != Activity.RESULT_OK) {
                        //nothing
                        Observable.empty<me.bemind.glitchappcore.Response<GlitchyBaseActivity, Bitmap>>()
                    }else {
                        val b = (response.targetUI() as GlitchyBaseActivity).getIOPresenter().ioLogic
                                .openImage(response.targetUI(),response.data()!!.file,w,h)
                        Observable.just(me.bemind.glitchappcore.Response((response.targetUI() as GlitchyBaseActivity), b))
                    }
                }
                /*.doOnNext { b -> b.activity.setImagebitmap(b.image); }
                .doOnError { t -> t.printStackTrace() }*/
                .doOnComplete { Log.i(TAG,"Load complete") }
                .subscribe({
                    b -> b.activity.setImage(b.image)
                },
                        {
                            t -> t.printStackTrace()
                            ioView?.showErrorGetImage(t)
                        })
    }
}
