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

    fun glitchImage() : Observable<Bitmap?>

    fun getStack():ArrayList<Image>

    fun setStack(list: List<Image>?)

    fun saveEffect()

    fun back() : Bitmap?

    fun canBack() : Boolean

    fun lastBitmap() : Bitmap?

    fun firstBitmap() : Bitmap?

    fun sizeHistory() : Int

    fun hasHistory() : Boolean

    fun anaglyphImage(progress:Int = 20) :Observable<Bitmap?>

}

class ImageLogic : IImageLogic{


    val stack =  LinkedStack<Image>()

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
        stack.push(Image(b,Effect.BASE,true))
        return b
    }

    override fun glitchImage(): Observable<Bitmap?> {

        val b : Bitmap?

        val lastImage = stack.peek()

        if(lastImage?.saved?:false){
            b = lastImage?.bitmap
        }else{
            stack.pop()
            b = stack.peek()?.bitmap
        }

        if(b!=null) {
            return Observable.defer {
                return@defer Observable.just(Glitcher.getGlitcher().corruption(b))
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

    override fun anaglyphImage( progress: Int ):Observable<Bitmap?> {
        val b : Bitmap?

        val lastImage = stack.peek()

        if(lastImage?.saved?:false){
            b = lastImage?.bitmap
        }else{
            stack.pop()
            b = stack.peek()?.bitmap
        }

        return Observable.defer {
            return@defer Observable.just(Glitcher.getGlitcher().anaglyph(b,progress))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap {
                        b ->
                        if (b != null) addBitmap(b,Effect.ANAGLYPH,false)
                        return@flatMap Observable.just(b)
                    }
        }

    }

    override fun saveEffect() {
        stack.peek()?.saved = true
    }

    private fun addBitmap(b: Bitmap, effect: Effect, saved:Boolean = true) {
        stack.push(Image(b,effect,saved))
        if(stack.size()>13) stack.removeOld()
    }

    override fun setStack(list: List<Image>?) {
        stack.addAll(list?:LinkedList<Image>())
    }

    override fun back(): Bitmap? {
        stack.pop()
        return stack.peek()?.bitmap
    }

    override fun getStack(): ArrayList<Image> {
        return stack.getAllAsList()
    }

    override fun canBack(): Boolean {
        return !(stack.isEmpty()||stack.size() == 1)
    }

    override fun lastBitmap(): Bitmap? {
        return stack.peek()?.bitmap
    }

    override fun firstBitmap(): Bitmap? {
        return stack.first()?.bitmap
    }

    override fun sizeHistory(): Int {
        return stack.size()
    }

    override fun hasHistory(): Boolean {
        return sizeHistory() > 0
    }
}
