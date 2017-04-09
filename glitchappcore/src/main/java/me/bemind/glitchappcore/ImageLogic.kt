package me.bemind.glitchappcore

import android.content.Context
import android.graphics.Bitmap
import com.github.oliveiradev.lib.RxPhoto
import com.github.oliveiradev.lib.shared.TypeRequest
import rx.Observable

/**
 * Created by angelomoroni on 04/04/17.
 */
interface IImageLogic{

    fun getImage(context: Context,
                 type:TypeRequest = TypeRequest.GALLERY,
                 w:Int = 1024, h:Int = 1024) : Observable<Bitmap>
}

class ImageLogic : IImageLogic{
    override fun getImage(context: Context, type: TypeRequest,
                          w:Int , h:Int ): Observable<Bitmap> {
        return RxPhoto.requestBitmap(context, type,w,h)
    }

}
