package me.bemind.glitchappcore

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable
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

    fun glitchImage(bitmap: Bitmap?) : Observable<Bitmap?>

    fun getStack():ArrayList<Bitmap>

    fun setStack(list: List<Bitmap>?)

    fun back() : Bitmap?

    fun canBack() : Boolean

    fun lastBitmap() : Bitmap?

    fun firstBitmap() : Bitmap?

    fun sizeHistory() : Int

    fun hasHistory() : Boolean

    fun anaglyphImage(progress:Int = 20, new: Boolean = false) :Bitmap?

}

class ImageLogic : IImageLogic{


    val stack =  LinkedStack<Bitmap>()

   /* override fun getImage(context: Context, type: TypeRequest,
                          w:Int , h:Int ): Observable<Bitmap> {
        return RxPhoto.requestBitmap(context, type,w,h)
                .flatMap {
                    b -> stack.push(b)
                    return@flatMap Observable.just(b)
                }
    }*/

    override fun getImage(context: Context, file: File,w: Int,h: Int): Bitmap {
        val uri = Uri.fromFile(file)

        val b = Utils.getBitmap(context,uri,w,h)
        stack.clear()
        stack.push(b)
        return b
    }

    override fun glitchImage(bitmap: Bitmap?): Observable<Bitmap?> {
        if(bitmap!=null) {
            return Observable.defer {
                return@defer Observable.just(Glitcher.getGlitcher().corruption(bitmap))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap {
                            b ->
                            if (b != null) addBitmap(b)
                            return@flatMap Observable.just(b)
                        }
            }
        }else{
            return Observable.error( RuntimeException("Bitmap null"))
        }
    }

    override fun anaglyphImage( progress: Int, new:Boolean ):Bitmap? {
        val b : Bitmap?
        if(new) {
            b = stack.peek()
        }else{
            stack.pop()
            b = stack.peek()
        }

        val b1 = Glitcher.getGlitcher().anaglyph(b,progress)

        stack.push(b1!!)

        return b1
    }

    private fun addBitmap(b: Bitmap) {
        stack.push(b)
        if(stack.size()>12) stack.removeOld()
    }

    override fun setStack(list: List<Bitmap>?) {
        stack.addAll(list?:LinkedList<Bitmap>())
    }

    override fun back(): Bitmap? {
        stack.pop()
        return stack.peek()
    }

    override fun getStack(): ArrayList<Bitmap> {
        return stack.getAllAsList()
    }

    override fun canBack(): Boolean {
        return !(stack.isEmpty()||stack.size() == 1)
    }

    override fun lastBitmap(): Bitmap? {
        return stack.peek()
    }

    override fun firstBitmap(): Bitmap? {
        return stack.first()
    }

    override fun sizeHistory(): Int {
        return stack.size()
    }

    override fun hasHistory(): Boolean {
        return sizeHistory() > 0
    }
}
