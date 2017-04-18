package me.bemind.glitchlibrary

import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater

/**
 * Created by angelomoroni on 09/04/17.
 */

class PickPhotoBottomSheet private constructor(){

    private var listener: OnPickPhotoListener? = null
    private var context: Context? = null

    private var mBottomSheet :BottomSheetDialog? = null

    companion object Creator {
        fun getPickPhotoBottomSheet(context:Context,listener: OnPickPhotoListener) : PickPhotoBottomSheet{
            val pickPhotoBottmSheet = PickPhotoBottomSheet()

            pickPhotoBottmSheet.context = context
            pickPhotoBottmSheet.listener = listener

            return pickPhotoBottmSheet
        }
    }

    fun dismiss() {
        mBottomSheet?.dismiss()
    }

   fun show() {
       mBottomSheet = BottomSheetDialog(context!!)

       val view = LayoutInflater.from(context)
               .inflate(R.layout.pick_photo_bottom_sheet,null, false)

       mBottomSheet?.setContentView(view)
       mBottomSheet?.setOnDismissListener { mBottomSheet = null }
       mBottomSheet?.show()

       view.findViewById(R.id.camera_text_view).setOnClickListener { listener?.openCamera() }
       view.findViewById(R.id.gallery_text_view).setOnClickListener { listener?.openGallery() }
   }

    interface OnPickPhotoListener{
        fun openCamera()
        fun openGallery()
    }

}

class SaveImageBottomSheet private constructor(){

    private var listener : OnSaveImageListener? = null
    private var context: Context? = null
    private var mBottomSheet :BottomSheetDialog? = null

    companion object Creator {
        fun getSaveImageBottomSheet(context:Context,listener: SaveImageBottomSheet.OnSaveImageListener) : SaveImageBottomSheet{
            val saveImagebottomSheet = SaveImageBottomSheet()

            saveImagebottomSheet.context = context
            saveImagebottomSheet.listener = listener

            return saveImagebottomSheet
        }
    }

    fun dismiss() {
        mBottomSheet?.dismiss()
    }

    fun show() {
        mBottomSheet = BottomSheetDialog(context!!)

        val view = LayoutInflater.from(context)
                .inflate(R.layout.save_image_bottom_sheet,null, false)

        mBottomSheet?.setContentView(view)
        mBottomSheet?.setOnDismissListener { mBottomSheet = null }
        mBottomSheet?.show()

        view.findViewById(R.id.share_text_view).setOnClickListener { listener?.shareImage() }
        view.findViewById(R.id.save_text_view).setOnClickListener { listener?.saveImage() }
    }


    interface OnSaveImageListener{
        fun saveImage()
        fun shareImage()
    }
}
