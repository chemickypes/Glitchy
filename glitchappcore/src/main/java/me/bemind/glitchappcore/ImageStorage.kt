package me.bemind.glitchappcore

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import me.bemind.glitch.Effect
import me.bemind.glitch.GlitcherUtil
import java.io.*

/**
 * Created by angelomoroni on 13/04/17.
 *
 * This file contains ImageStorage class, a cache system of bitmap
 */

object ImageStorage  {

    private val dimCache = 8 * 1024 * 1024
    private val MAX_SIZE_STACK: Int = 12
    val stack : LinkedStack<ImageDescriptor> = LinkedStack()
    private var lruCache = LruCache<String,Bitmap>(dimCache)

    var context : Context? = null

    private val stackLenght: Int
    get() = stack.size()

    fun addBitmap(bitmap: Bitmap, effect: Effect, base:Boolean = false) {

        var index : Int = 0

       if(!base) {
            val lastImage = stack.peek()
            index = (((lastImage?.index ?: 0) + 1) % (MAX_SIZE_STACK-1))+1


            if (stackLenght == 12) {
                val imD = stack.removeOld()
                if (imD != null) lruCache.remove(imD.imageName)
            }
        }

        val imageName = "image_$index.jpg"

        stack.push(ImageDescriptor(index,imageName,effect,base))
        lruCache.put(imageName,bitmap)

        createTemporaryFile(imageName,bitmap)
    }

    fun getLastBitmap(): Bitmap?{
        return getBitmap(stack.peek()?.imageName?:"")
    }

    fun getImageToPutEffect(): Bitmap?{
        val lastImage = stack.peek()

        if(lastImage?.saved?:false){
            return getBitmap(lastImage?.imageName?:"")
        }else{
            removeLast()
            return getBitmap(stack.peek()?.imageName?:"")

        }
    }

    /**
     * save effect to last image
     */
    fun saveEffect() {
        stack.peek()?.saved = true
    }

    fun back() : Bitmap? {
        removeLast()
        return getBitmap(stack.peek()?.imageName?:"")
    }

    fun firstBitmap() :Bitmap?  = getBitmap(stack.first()?.imageName?:"")

    fun size() = stackLenght

    fun canBack() = !(stack.isEmpty()|| stackLenght == 1)

    fun clear() {
        stack.clear()
        lruCache = LruCache<String,Bitmap>(dimCache)
        Utils.deleteCache(context?.cacheDir?: File("void"))
    }

    fun removeLast() {
        val ls = stack.pop()
        lruCache.remove(ls?.imageName)
    }

    fun  removeLastNonSaved() {
        val last = stack.peek()
        if(!(last?.saved?:true)){
            removeLast()
        }
    }

    private fun getBitmap(imageName:String) : Bitmap?{
        var b = lruCache.get(imageName)

        if(b == null){
            val f = File(context?.cacheDir,imageName)
            b = Utils.getBitmapFromFile(f)
            lruCache.put(imageName,b)
        }

        return b
    }

    private fun createTemporaryFile(fileName:String,bitmap: Bitmap){
        val s = fileName.split(".")
        val f = createTempFile(s[0],s[1], context?.cacheDir)

        val b = BufferedOutputStream(f.outputStream())
        b.write(GlitcherUtil.byteArrayFromBitmap(bitmap))
        b.close()
    }

}