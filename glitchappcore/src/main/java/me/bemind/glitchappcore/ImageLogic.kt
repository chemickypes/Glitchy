package me.bemind.glitchappcore

import android.content.Context
import android.graphics.Bitmap
import com.github.oliveiradev.lib.RxPhoto
import com.github.oliveiradev.lib.shared.TypeRequest
import me.bemind.glitch.Glitcher
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.ArrayList

/**
 * Created by angelomoroni on 04/04/17.
 */
interface IImageLogic{

    fun getImage(context: Context,
                 type:TypeRequest = TypeRequest.GALLERY,
                 w:Int = 1024, h:Int = 1024) : Observable<Bitmap>

    fun glitchImage(bitmap: Bitmap?) : Observable<Bitmap?>

    fun getStack():ArrayList<Bitmap>

    fun setStack(list: List<Bitmap>)

    fun back() : Bitmap?

    fun canBack() : Boolean

    fun lastBitmap() : Bitmap?

    fun firstBitmap() : Bitmap?

    fun sizeHistory() : Int

    fun hasHistory() : Boolean
}

class ImageLogic : IImageLogic{


    val stack =  LinkedStack<Bitmap>()

    override fun getImage(context: Context, type: TypeRequest,
                          w:Int , h:Int ): Observable<Bitmap> {
        return RxPhoto.requestBitmap(context, type,w,h)
                .flatMap {
                    b -> stack.push(b)
                    return@flatMap Observable.just(b)
                }
    }

    override fun glitchImage(bitmap: Bitmap?): Observable<Bitmap?> {
        return Observable.defer {
            return@defer Observable.just(Glitcher.getGlitcher().corruption(bitmap))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap {
                        b -> if(b!=null) stack.push(b)
                        return@flatMap Observable.just(b)
                    }
        }
    }

    override fun setStack(list: List<Bitmap>) {
        stack.addAll(list)
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
        return sizeHistory() >0
    }
}
