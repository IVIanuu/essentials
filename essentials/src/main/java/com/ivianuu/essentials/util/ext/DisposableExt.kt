package com.ivianuu.essentials.util.ext

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.SimpleLifecycleObserver
import io.reactivex.disposables.Disposable

fun Disposable.disposedWith(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
) = apply {
    owner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
        override fun onAny(owner: LifecycleOwner, e: Lifecycle.Event) {
            if (event == e) {
                owner.lifecycle.removeObserver(this)
                dispose()
            }
        }
    })
}