package com.ivianuu.essentials.util.lifecycle

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

interface LifecycleCoroutineScope : CoroutineScope, LifecycleOwner {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job: Job

    fun initCoroutineScope() {
        lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                job.cancel()
            }
        })
    }
}