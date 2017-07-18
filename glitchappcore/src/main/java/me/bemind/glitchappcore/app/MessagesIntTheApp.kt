package me.bemind.glitchappcore.app

import io.reactivex.subjects.BehaviorSubject

/**
 * Created by angelomoroni on 18/07/17.
 */

object ProgressUpdate {
    val progressSubject :  BehaviorSubject<Float> = BehaviorSubject.create()

    fun updateProgress(progress:Float){
        progressSubject.onNext(progress)
    }
}