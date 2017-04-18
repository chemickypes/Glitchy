package me.bemind.glitchappcore

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import me.bemind.glitchappcore.io.IIOPresenter
import me.bemind.glitchappcore.io.IIOView

/**
 * Created by angelomoroni on 10/04/17.
 */

abstract class  GlitchyBaseActivity : AppCompatActivity(), IImageView,IIOView {

    abstract fun getImagePresenter() : IImagePresenter
    abstract fun getIOPresenter() : IIOPresenter

    var  retainedFragment : RetainedFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fm = fragmentManager

        retainedFragment = fm.findFragmentByTag(RetainedFragment.TAG.TAG) as RetainedFragment?

        if(retainedFragment==null){
            retainedFragment = RetainedFragment()
            fm.beginTransaction().add(retainedFragment,RetainedFragment.TAG.TAG).commit()
        }
    }
}
