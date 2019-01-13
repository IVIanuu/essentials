package com.ivianuu.essentials.ui.mvrx

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.closeable.Closeable
import com.ivianuu.injekt.InjektTrait

private val PENDING_INVALIDATES = HashSet<Int>()
private val HANDLER = Handler(Looper.getMainLooper(), Handler.Callback { message ->
    val view = message.obj as MvRxView
    PENDING_INVALIDATES.remove(System.identityHashCode(view))

    if (view.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        view.invalidate()
    }

    true
})

interface MvRxView : InjektTrait, LifecycleOwner, ViewModelStoreOwner {

    fun invalidate()

    fun postInvalidate() {
        if (PENDING_INVALIDATES.add(System.identityHashCode(this))) {
            HANDLER.sendMessage(Message.obtain(HANDLER, System.identityHashCode(this), this))
        }
    }

    fun <S> MvRxViewModel<S>.subscribe(subscriber: (S) -> Unit): Closeable =
        subscribe(this@MvRxView, subscriber)

}