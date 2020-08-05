package com.csw.bluetooth.utils

import android.animation.Animator
import android.view.animation.Animation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@ExperimentalCoroutinesApi
suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> {
    //当协程被取消，则取消动画
    it.invokeOnCancellation {
        cancel()
    }
    addListener(object : Animator.AnimatorListener {
        private var endedSuccessfully = true

        override fun onAnimationStart(animation: Animator?) {
        }

        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
            endedSuccessfully = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            removeListener(this)
            if (it.isActive) {
                if (endedSuccessfully) {
                    it.resume(Unit) {
                        it.printStackTrace()
                    }
                } else {
                    it.cancel()
                }
            }
        }
    })


}