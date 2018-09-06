package com.ivianuu.essentials.util.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.lifecycle.SimpleLifecycleObserver
import io.reactivex.disposables.Disposable

fun Disposable.disposedWith(
    owner: LifecycleOwner,
    event: Lifecycle.Event = owner.lifecycle.correspondingEvent()
) = apply {
    owner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
        override fun onAny(owner: LifecycleOwner, e: Lifecycle.Event) {
            if (event == e || e == Lifecycle.Event.ON_DESTROY) dispose()
        }
    })
}