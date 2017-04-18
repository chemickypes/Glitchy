package me.bemind.glitchappcore.io

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import me.bemind.glitchappcore.Utils
import java.io.File

/**
 * Created by angelomoroni on 18/04/17.
 */

interface IIOLogic {

    fun openImage(context: Context,
                  file: File, w:Int = 1024, h:Int =  1024) : Bitmap

    fun saveImage(bitmap: Bitmap)
}

class IOLogic : IIOLogic{

    override fun saveImage(bitmap: Bitmap) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openImage(context: Context,file: File,w: Int,h: Int) :Bitmap{
        val uri = Uri.fromFile(file)
        val b = Utils.getBitmap(context,uri,w,h)
        return b
    }

}