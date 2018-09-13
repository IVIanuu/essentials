package com.ivianuu.essentials.util.lifecycle

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Job

/**
 * Lifecycle aware job
 */
class LifecycleJob(owner: LifecycleOwner, parent: Job? = null) : Job by Job(parent) {
    init {
        owner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                cancel()
                owner.lifecycle.removeObserver(this)
            }
        })
    }
}