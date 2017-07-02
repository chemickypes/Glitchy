package me.bemind.glitchappcore.io

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.sromku.simple.storage.ExternalStorage
import com.sromku.simple.storage.SimpleStorage
import com.sromku.simple.storage.Storage
import me.bemind.glitchappcore.Utils
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.provider.MediaStore
import android.content.ContentValues



/**
 * Created by angelomoroni on 18/04/17.
 */

interface IIOLogic {

    fun openImage(context: Context,
                  file: File, w:Int = 1024, h:Int =  1024) : Bitmap

    fun saveImage(bitmap: Bitmap,context: Context):String?

    fun openImage(context: Context,uri:Uri,w:Int = 1024, h:Int =  1024):Bitmap

    fun uriFromFileName(fileName:String?) : Uri?
}

class IOLogic : IIOLogic{

    private val DIR_NAME: String? = "Glitchy"

    override fun saveImage(bitmap: Bitmap,context: Context) :String?{

        if(SimpleStorage.isExternalStorageWritable()){
            val storage = getStorage()
            createImageDir(storage)
            val byte = compressBitmap(bitmap)
            val fileName = getCustomFileName()
           val b = storage.createFile(DIR_NAME,fileName,byte)
            if(b){
                saveImageToContentResolver(storage.getFile(DIR_NAME,fileName).absolutePath,context)
                return fileName
            }else{
                return null
            }
        }else{
            return null
        }
    }

    private fun saveImageToContentResolver(absolutePath: String?, context: Context) {
        val values = ContentValues()

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.MediaColumns.DATA, absolutePath)

        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }


    override fun openImage(context: Context,file: File,w: Int,h: Int) :Bitmap{
        val uri = Uri.fromFile(file)
        /*val b = Utils.getBitmap(context,uri,w,h)
        return b*/
        return openImage(context,uri,w,h)
    }

    override fun openImage(context: Context, uri: Uri, w: Int, h: Int): Bitmap {
        return Utils.getBitmap(context,uri,w,h)
    }

    override fun uriFromFileName(fileName: String?): Uri? {
        if(SimpleStorage.isExternalStorageWritable()){
            val storage = getStorage()
            if(storage.isFileExist(DIR_NAME,fileName)){
                val uri = Uri.fromFile(storage.getFile(DIR_NAME,fileName))
                return uri
            }else{
                return null
            }
        }else{
            return null
        }
    }

    private fun getStorage(): ExternalStorage {
        return SimpleStorage.getExternalStorage(Environment.DIRECTORY_PICTURES)
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