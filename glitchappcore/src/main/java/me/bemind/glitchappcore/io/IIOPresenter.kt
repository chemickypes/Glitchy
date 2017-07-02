package me.bemind.glitchappcore.io

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import com.miguelbcr.ui.rx_paparazzo2.entities.Response
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.bemind.glitchappcore.GlitchyBaseActivity
import java.lang.RuntimeException

/**
 * Created by angelomoroni on 18/04/17.
 */

interface IIOView{
    fun setImage(bitmap:Bitmap)
    fun showErrorGetImage(t:Throwable)
    fun showSuccessSaveImage(fileName:String)
    fun showErrorSaveImage(t: Throwable)
    fun showErrorImageShare(t:Throwable)
    fun canShareImage(uri: Uri)
}

interface IIOPresenter {

    var BASE_DIM : Int

    var ioLogic : IIOLogic

    var ioView : IIOView?

    enum class TypePick {
        CAMERA,GALLERY
    }
    fun openImage(activity: GlitchyBaseActivity,typePick: TypePick,w:Int = BASE_DIM,h:Int = BASE_DIM)
    fun saveImage(bitmap: Bitmap?,context: Context)
    fun shareImage(bitmap: Bitmap?,context: Context)
    fun openImage(context: Context,intent: Intent?,w:Int = BASE_DIM,h:Int = BASE_DIM)
}

class IOPresenter : IIOPresenter {


    override var BASE_DIM: Int = 1024
    set(value) {
        /*nothing*/
    }
    private var disposable: Disposable? = null

    override var ioLogic: IIOLogic = IOLogic()
    set(value) {/*nothing to assign */}

    private val TAG = "IOPResenter"

    override var ioView : IIOView? = null
    set(value) {
        if(value == null ) disposable?.dispose()
        field = value
    }


    override fun openImage(activity: GlitchyBaseActivity,typePick: IIOPresenter.TypePick,w:Int,h:Int) {
        when (typePick){
            IIOPresenter.TypePick.CAMERA -> openImageFromCamera(activity,w,h)
            else -> openImageFromGallery(activity, w, h)
        }
    }

    override fun openImage(context:Context,intent: Intent?, w: Int, h: Int) {
        intent?.let {
            val imageUri : Uri = when(it.action){
                Intent.ACTION_EDIT -> it.data
                else -> it.getParcelableExtra(Intent.EXTRA_STREAM)!!
            }
            disposable = Observable.fromCallable { ioLogic.openImage(context,imageUri,w,h) }
                    .subscribe(
                            { b -> ioView?.setImage(b)},
                            {e -> ioView?.showErrorGetImage(e)}
                    )
        }

    }



    override fun saveImage(bitmap: Bitmap?,context: Context) {
        if(bitmap!=null) {
            disposable = handleSaveImageObservable(bitmap,context )
                    .subscribe(
                            {
                                b ->
                                if (b!=null) {
                                    ioView?.showSuccessSaveImage(b)
                                } else {
                                    ioView?.showErrorSaveImage(RuntimeException("Errore salvataggio"))
                                }
                            },
                            {
                                t ->
                                ioView?.showErrorSaveImage(t)
                            })
        }else{
            ioView?.showErrorSaveImage(RuntimeException("Immagine null"))
        }
    }

    override fun shareImage(bitmap: Bitmap?,context: Context) {
        if(bitmap!=null){
            disposable = handleSaveImageObservable(bitmap,context )
                    .subscribe(
                            {
                                fileName ->
                                if (fileName!=null){
                                    val uri = ioLogic.uriFromFileName(fileName)
                                    if(uri!=null) {
                                        ioView?.canShareImage(uri)
                                    }else{
                                        ioView?.showErrorImageShare(RuntimeException("UriNull"))
                                    }
                                }else{
                                    ioView?.showErrorImageShare(RuntimeException("FileNAmeNull"))
                                }
                            },
                            {
                                t -> ioView?.showErrorImageShare(t)
                            }
                    )
        }else{
            ioView?.showErrorImageShare(RuntimeException("Image null"))
        }
    }


    private fun openImageFromGallery(activity: GlitchyBaseActivity,w:Int,h:Int) {
        handleObservable( RxPaparazzo.single(activity).usingGallery(),w,h)
    }



    private fun openImageFromCamera(activity: GlitchyBaseActivity,w:Int,h:Int) {
        handleObservable(RxPaparazzo.single(activity)
                .usingCamera(),w,h)

    }

    fun handleSaveImageObservable(bitmap:Bitmap,context: Context) : Observable<String?>{
        return Observable.fromCallable { ioLogic.saveImage(bitmap,context) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
