package me.bemind.glitchappcore.history

import android.graphics.Bitmap

/**
 * Created by angelomoroni on 14/04/17.
 */

interface IHistoryPresenter {

    var hasHistory :Boolean

    var canBack : Boolean

    fun clearHistory()

    fun addImage(bitmap: Bitmap)

    fun back()


}
