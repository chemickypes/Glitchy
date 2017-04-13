package me.bemind.glitchappcore

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache

/**
 * Created by angelomoroni on 13/04/17.
 *
 * This file contains ImageStorage class, a cache system of bitmap
 */

object ImageStorage  {

    private val dimCache = 8 * 1024 * 1024
    private val MAX_SIZE_STACK: Int = 12
    val stack : LinkedStack<ImageDescriptor> = LinkedStack()
    private val lruCache = LruCache<String,Bitmap>(dimCache)

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
    }

    fun getLastBitmap(): Bitmap?{
        return lruCache.get(stack.peek()?.imageName?:"")
    }

    fun getImageToPutEffect(): Bitmap?{
        val lastImage = stack.peek()

        if(lastImage?.saved?:false){
            return lruCache.get(lastImage?.imageName)
        }else{
            removeLast()
            return lruCache.get(stack.peek()?.imageName)

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
        return lruCache.get(stack.peek()?.imageName)
    }

    fun firstBitmap() :Bitmap?  = lruCache.get(stack.first()?.imageName)

    fun size() = stackLenght

    fun canBack() = !(stack.isEmpty()|| stackLenght == 1)

    fun clear() {
        stack.clear()
    }

    fun removeLast() {
        val ls = stack.pop()
        lruCache.remove(ls?.imageName)
    }


}