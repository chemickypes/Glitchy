package me.bemind.glitchappcore

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable
import me.bemind.glitch.Effect
import me.bemind.glitch.Glitcher
import java.io.File
import java.lang.RuntimeException
import java.util.*

/**
 * Created by angelomoroni on 04/04/17.
 */
interface IImageLogic{

    fun getImage(context: Context,
                 file: File, w:Int = 1024,h:Int =  1024) : Bitmap

    fun glitchImage() : Observable<Bitmap?>

    fun getStack():ArrayList<ImageDescriptor>

    fun setStack(list: List<ImageDescriptor>?)

    fun saveEffect()

    fun back() : Bitmap?

    fun canBack() : Boolean

    fun lastBitmap() : Bitmap?

    fun firstBitmap() : Bitmap?

    fun sizeHistory() : Int

    fun hasHistory() : Boolean

    //fun anaglyphImage(progress:Int = 20) :Observable<Bitmap?>

    fun anaglyphImage(progress:Int = 20, init:Boolean = false) :Bitmap?


}

class ImageLogic : IImageLogic{



    private val imageStorage = ImageStorage

    private val glitcher =  Glitcher


    override fun getImage(context: Context, file: File,w: Int,h: Int): Bitmap {
        val uri = Uri.fromFile(file)

        val b = Utils.getBitmap(context,uri,w,h)
        imageStorage.clear()
        imageStorage.addBitmap(b, Effect.BASE,true)
        return b
    }

    override fun glitchImage(): Observable<Bitmap?> {

        val b : Bitmap? = getImageToPutEffect()

        if(b!=null) {
            return Observable.defer {
                return@defer Observable.just(glitcher.corruption(b))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap {
                            b ->
                            if (b != null) addBitmap(b,Effect.GLITCH,false)
                            return@flatMap Observable.just(b)
                        }
            }
        }else{
            return Observable.error( RuntimeException("Bitmap null"))
        }
    }




    override fun anaglyphImage(progress: Int ,init: Boolean):/*Observable<Bitmap?>*/Bitmap? {


        if(init){
            val bo : Bitmap? = getImageToPutEffect()
            glitcher.initAnaglyph(bo)
        }

        val b = glitcher.anaglyph(progress)
        if (b != null){
            imageStorage.removeLastNonSaved()
            addBitmap(b,Effect.ANAGLYPH,false)
        }

        return b

    }

    override fun saveEffect() {
        imageStorage.saveEffect()
    }

    private fun addBitmap(b: Bitmap, effect: Effect, saved:Boolean = true) {
        imageStorage.addBitmap(b,effect,saved)
        //if(stack.size()>13) stack.removeOld()
    }

    override fun setStack(list: List<ImageDescriptor>?) {
        imageStorage.stack.addAll(list?:LinkedList<ImageDescriptor>())
    }

    override fun back(): Bitmap?  = imageStorage.back()

    override fun getStack(): ArrayList<ImageDescriptor> = imageStorage.stack.getAllAsList()

    override fun canBack(): Boolean  = imageStorage.canBack()

    override fun lastBitmap(): Bitmap? = imageStorage.getLastBitmap()

    override fun firstBitmap(): Bitmap?  = imageStorage.firstBitmap()

    override fun sizeHistory(): Int = imageStorage.size()

    override fun hasHistory(): Boolean = imageStorage.size()>0

    private fun getImageToPutEffect() : Bitmap? = imageStorage.getImageToPutEffect()
}
