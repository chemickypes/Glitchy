package me.bemind.glitchappcore

import android.support.v7.app.AppCompatActivity

/**
 * Created by angelomoroni on 10/04/17.
 */

abstract class  GlitchyBaseActivity : AppCompatActivity(), IImageView {

    abstract fun getImagePresenter() : IImagePresenter
}
