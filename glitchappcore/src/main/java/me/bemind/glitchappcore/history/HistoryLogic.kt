package me.bemind.glitchappcore.history

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val PREFERENCES_NAME = "history_preferences"
    private val STACK_K: String? = "stack_k"

    private val imageStorage = ImageStorage
    private val historyPref : SharedPreferences by lazy {

        context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE)
    }

    init {
        imageStorage.context = context
    }

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
        historyPref.edit().putString(STACK_K,"").apply()
    }

    override fun setStack(list: List<ImageDescriptor>?) {
        imageStorage.stack.clear()
        if(list !=null) {
            imageStorage.stack.addAll(list )
        }else{

            imageStorage.stack.addAll(getListFromStorage())
        }
    }



    override fun back(): Bitmap?  = imageStorage.back()

    override fun getStack(): ArrayList<ImageDescriptor> {
        val l = imageStorage.stack.getAllAsList()

        historyPref.edit().putString(STACK_K,Gson().toJson(l)).apply()

        return l
    }

    override fun canBack(): Boolean  = imageStorage.canBack()




    private fun getListFromStorage(): Collection<ImageDescriptor> {
        val list = historyPref.getString(STACK_K,"")

        if (list.isEmpty()){
            return LinkedList()
        }else{
            val listType = object : TypeToken<LinkedList<ImageDescriptor>>() {}.type
            return Gson().fromJson(list,listType)
        }
    }

}