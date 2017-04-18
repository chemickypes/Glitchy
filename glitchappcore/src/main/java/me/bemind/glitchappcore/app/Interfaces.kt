package me.bemind.glitchappcore.app

import android.content.res.Configuration
import android.os.Bundle
import me.bemind.glitchappcore.EffectState
import me.bemind.glitchappcore.GlitchyBaseActivity
import me.bemind.glitchappcore.State

/**
 * Created by angelomoroni on 18/04/17.
 */

interface IAppView {
    fun updateState(state: State)
    fun restoreView(effectState: EffectState?)
}

interface IAppPresenter {
    var modState : State
    var appView : IAppView?

    var effectState : EffectState?

    fun saveInstanceState(activity: GlitchyBaseActivity, outState: Bundle?)
    fun restoreInstanceState(activity: GlitchyBaseActivity, savedInstanceState: Bundle?)
    fun onBackPressed() :Boolean
    fun onConfigurationChanged(newConfig: Configuration?)
    fun onResume()

}
interface IAppLogic
