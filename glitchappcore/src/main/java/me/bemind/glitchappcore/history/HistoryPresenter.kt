package me.bemind.glitchappcore.history

import android.graphics.Bitmap
import me.bemind.glitchappcore.ImageDescriptor
import java.util.ArrayList

/**
 * Created by angelomoroni on 14/04/17.
 */

interface IHistoryPresenter {

    var hasHistory :Boolean

    var canBack : Boolean

    fun clearHistory()

    fun addImage(bitmap: Bitmap,newPhoto:Boolean = false)

    fun back()

    fun getHistoryToSave(): ArrayList<ImageDescriptor>?

    fun restoreHistory(list: ArrayList<ImageDescriptor>?,setImage:Boolean = true)


}

class HistoryPresenter : IHistoryPresenter {

    val historyLogic  = HistoryLogic()
    var historyView : IHistoryView? = null

    override var hasHistory: Boolean
        get() = historyLogic.hasHistory
        set(value) {/*nothing*/}
    override var canBack: Boolean
        get() = historyLogic.canBack()
        set(value) {}

    override fun clearHistory() {
       historyLogic.clearHistory()
    }

    override fun addImage(bitmap: Bitmap,newPhoto: Boolean) {
        historyLogic.addBitmap(bitmap,newPhoto)
    }

    override fun back() {
        historyView?.setPreviousImage(historyLogic.back())
    }

    override fun getHistoryToSave(): ArrayList<ImageDescriptor>? {
        return historyLogic.getStack()
    }

    override fun restoreHistory(list: ArrayList<ImageDescriptor>?,setImage: Boolean) {
        historyLogic.setStack(list)
        if(setImage) historyView?.setPreviousImage(historyLogic.lastBitmap,true)
    }
}
