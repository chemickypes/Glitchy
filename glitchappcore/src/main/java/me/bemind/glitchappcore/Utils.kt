package me.bemind.glitchappcore

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import java.io.File

import java.io.IOException



/**
 * Created by angelomoroni on 05/04/17.
 */

object Utils {

    @Throws(IOException::class)
    fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String?): Bitmap {

        val ei = ExifInterface(image_absolute_path)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return rotate(bitmap, 90f)

            ExifInterface.ORIENTATION_ROTATE_180 -> return rotate(bitmap, 180f)

            ExifInterface.ORIENTATION_ROTATE_270 -> return rotate(bitmap, 270f)

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> return flip(bitmap, true, false)

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> return flip(bitmap, false, true)

            else -> return bitmap
        }
    }


    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {

        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {

        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun getBitmap(context: Context, uri: Uri, width: Int, height: Int): Bitmap {
        var path: String?
        try {
            path = Utils.getRealPathFromURI(uri, context) //from Gallery
        } catch (e: Exception) {
            path = null
        }

        if (path == null)
            path = uri.path

        val iOptions = BitmapFactory.Options()
        iOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, iOptions)

        iOptions.inSampleSize = Utils.calculateInSampleSize(iOptions, width, height)
        iOptions.inJustDecodeBounds = false

        var original = BitmapFactory.decodeFile(path, iOptions)

        try {
            original = Utils.modifyOrientation(original, path)
        } catch (e: IOException) {
        }

        return original


    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {

                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun getRealPathFromURI(contentUri: Uri, context: Context): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null) ?: return null
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(column_index)
        cursor.close()
        return s
    }

    fun getBitmapFromFile(image:File) :Bitmap?{
        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(image.absolutePath, bmOptions)

        return bitmap
    }
    fun deleteCache(dir:File):Boolean{
        if ( dir.isDirectory()) {
        val children = dir.list()
            children.forEach { fi ->
                val success = deleteCache(File(dir,fi))
                if(!success) return false
            }
            return dir.delete()
        } else if( dir.isFile()) {
            return dir.delete()
        } else {
            return false
        }
    }
}
