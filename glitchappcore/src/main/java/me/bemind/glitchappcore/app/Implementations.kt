package me.bemind.glitchappcore.app

import android.content.res.Configuration
import android.os.Bundle
import me.bemind.glitchappcore.GlitchyBaseActivity
import me.bemind.glitchappcore.State

/**
 * Created by angelomoroni on 18/04/17.
 */

class AppPresenter : IAppPresenter {

    private val STATE_K = "state_k"

    override var modState: State = State.BASE
        set(value) {
            field = value
            appView?.updateState(field)
        }

    override var appView: IAppView? = null

    override fun saveInstanceState(activity: GlitchyBaseActivity, outState: Bundle?) {
        outState?.putSerializable(STATE_K,modState)
    }

    override fun restoreInstanceState(activity: GlitchyBaseActivity, savedInstanceState: Bundle?) {
        if(savedInstanceState?.containsKey(STATE_K)?:false) modState = savedInstanceState?.getSerializable(STATE_K) as State
    }

    override fun onBackPressed(): Boolean {
        if(modState!=State.BASE){
            modState = State.BASE
            return true
        }else{
            return false
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
    }

}
