package me.bemind.glitchappcore.history

import android.content.Context
import android.graphics.Bitmap
import me.bemind.glitch.Effect
import me.bemind.glitchappcore.ImageDescriptor
import me.bemind.glitchappcore.ImageStorage
import java.util.*

/**
 * Created by angelomoroni on 14/04/17.
 */
interface IHistoryLogic {
    fun getStack(): ArrayList<ImageDescriptor>

    fun setStack(list: List<ImageDescriptor>?)

    fun back() : Bitmap?

    fun canBack() : Boolean

    fun addBitmap(bitmap: Bitmap, newImage:Boolean = false)

    fun clearHistory()

    var lastBitmap : Bitmap?

    var firstBitmap : Bitmap?

    var sizeHistory : Int

    var hasHistory : Boolean
}

class HistoryLogic(val context:Context) : IHistoryLogic {

    private val imageStorage = ImageStorage

    override var lastBitmap: Bitmap?
        get() = imageStorage.getLastBitmap()
        set(value) {/*nothing*/ }

    override var firstBitmap: Bitmap?
        get() = imageStorage.firstBitmap()
        set(value) {/*nothing*/}

    override var sizeHistory: Int
        get() = imageStorage.size()
        set(value) {/*nothing*/}

    override var hasHistory: Boolean
        get() = sizeHistory>0
        set(value) {/*nothing*/}

    override fun addBitmap(bitmap: Bitmap,newImage: Boolean) {
        if(newImage) clearHistory()
        imageStorage.addBitmap(bitmap,Effect.BASE,newImage)
    }

    override fun clearHistory() {
        imageStorage.clear()
    }

    override fun setStack(list: List<ImageDescriptor>?) {
        imageStorage.stack.addAll(list?: LinkedList<ImageDescriptor>())
    }

    override fun back(): Bitmap?  = imageStorage.back()

    override fun getStack(): ArrayList<ImageDescriptor> = imageStorage.stack.getAllAsList()

    override fun canBack(): Boolean  = imageStorage.canBack()



}