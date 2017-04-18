package me.bemind.glitchappcore.io

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.sromku.simple.storage.SimpleStorage
import com.sromku.simple.storage.Storage
import me.bemind.glitchappcore.Utils
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by angelomoroni on 18/04/17.
 */

interface IIOLogic {

    fun openImage(context: Context,
                  file: File, w:Int = 1024, h:Int =  1024) : Bitmap

    fun saveImage(bitmap: Bitmap):Boolean

    fun uriFromFileName(fileName:String?) : Uri?
}

class IOLogic : IIOLogic{

    private val DIR_NAME: String? = "Glitchy"

    override fun saveImage(bitmap: Bitmap) :Boolean{

        if(SimpleStorage.isExternalStorageWritable()){
            val storage = SimpleStorage.getExternalStorage()
            createImageDir(storage)
            val byte = compressBitmap(bitmap)
           return storage.createFile(DIR_NAME,getCustomFileName(),byte)
        }else{
            return false
        }
    }




    override fun openImage(context: Context,file: File,w: Int,h: Int) :Bitmap{
        val uri = Uri.fromFile(file)
        val b = Utils.getBitmap(context,uri,w,h)
        return b
    }

    override fun uriFromFileName(fileName: String?): Uri? {
        if(SimpleStorage.isExternalStorageWritable()){
            val storage = SimpleStorage.getExternalStorage()
            if(storage.isFileExist(DIR_NAME,fileName)){
                return Uri.fromFile(File(DIR_NAME,fileName))
            }else{
                return null
            }
        }else{
            return null
        }
    }

    private fun getCustomFileName(): String? {
        val dateFormat = SimpleDateFormat("yyyMMdd_HHmmss",Locale.ITALY)
        return "image_"+dateFormat.format(Date())+".jpg"
    }

    private fun createImageDir(storage:Storage){
        if(!storage.isDirectoryExists(DIR_NAME)){
            storage.createDirectory(DIR_NAME)
        }
    }

    private fun compressBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return  stream.toByteArray()
    }

}