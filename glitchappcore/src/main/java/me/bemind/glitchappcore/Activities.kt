package me.bemind.glitchappcore

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by angelomoroni on 10/04/17.
 */

abstract class  GlitchyBaseActivity : AppCompatActivity(), IImageView {

    abstract fun getImagePresenter() : IImagePresenter

    var  retainedFragment : RetainedFragment? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val fm = fragmentManager

        retainedFragment = fm.findFragmentByTag(RetainedFragment.TAG.TAG) as RetainedFragment

        if(retainedFragment==null){
            retainedFragment = RetainedFragment()
            fm.beginTransaction().add(retainedFragment,RetainedFragment.TAG.TAG).commit()
        }
    }
}
