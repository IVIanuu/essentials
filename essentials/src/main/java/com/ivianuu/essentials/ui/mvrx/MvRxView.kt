package com.ivianuu.essentials.ui.mvrx

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KProperty1

private val PENDING_INVALIDATES = HashSet<Int>()
private val HANDLER = Handler(Looper.getMainLooper(), Handler.Callback { message ->
    val view = message.obj as MvRxView
    PENDING_INVALIDATES.remove(System.identityHashCode(view))

    if (view.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        view.invalidate()
    }

    true
})

interface MvRxView : LifecycleOwner, ViewModelStoreOwner {

    fun invalidate()

    fun postInvalidate() {
        if (PENDING_INVALIDATES.add(System.identityHashCode(this))) {
            HANDLER.sendMessage(Message.obtain(HANDLER, System.identityHashCode(this), this))
        }
    }

    fun <S : MvRxState> MvRxViewModel<S>.subscribe(subscriber: (S) -> Unit) =
        subscribe(this@MvRxView, subscriber)

    fun <S : MvRxState, A> MvRxViewModel<S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        subscriber: (A) -> Unit
    ) = selectSubscribe(this@MvRxView, prop1, subscriber)

    fun <S : MvRxState, A, B> MvRxViewModel<S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        subscriber: (A, B) -> Unit
    ) = selectSubscribe(this@MvRxView, prop1, prop2, subscriber)

    fun <S : MvRxState, A, B, C> MvRxViewModel<S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        subscriber: (A, B, C) -> Unit
    ) = selectSubscribe(this@MvRxView, prop1, prop2, prop3, subscriber)

    fun <S : MvRxState, A, B, C, D> MvRxViewModel<S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        subscriber: (A, B, C, D) -> Unit
    ) = selectSubscribe(this@MvRxView, prop1, prop2, prop3, prop4, subscriber)

    fun <S : MvRxState, A, B, C, D, E> MvRxViewModel<S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        subscriber: (A, B, C, D, E) -> Unit
    ) = selectSubscribe(this@MvRxView, prop1, prop2, prop3, prop4, prop5, subscriber)

    fun <S : MvRxState, A, B, C, D, E, F> MvRxViewModel<S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        subscriber: (A, B, C, D, E, F) -> Unit
    ) = selectSubscribe(this@MvRxView, prop1, prop2, prop3, prop4, prop5, prop6, subscriber)

    fun <S : MvRxState, A, B, C, D, E, F, G> MvRxViewModel<S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        prop7: KProperty1<S, G>,
        subscriber: (A, B, C, D, E, F, G) -> Unit
    ) = selectSubscribe(this@MvRxView, prop1, prop2, prop3, prop4, prop5, prop6, prop7, subscriber)

    fun <S : MvRxState, A, B, C, D, E, F, G, H> MvRxViewModel<S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        prop7: KProperty1<S, G>,
        prop8: KProperty1<S, H>,
        subscriber: (A, B, C, D, E, F, G, H) -> Unit
    ) = selectSubscribe(
        this@MvRxView,
        prop1,
        prop2,
        prop3,
        prop4,
        prop5,
        prop6,
        prop7,
        prop8,
        subscriber
    )

    fun <S : MvRxState, A, B, C, D, E, F, G, H, I> MvRxViewModel<S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        prop7: KProperty1<S, G>,
        prop8: KProperty1<S, H>,
        prop9: KProperty1<S, I>,
        subscriber: (A, B, C, D, E, F, G, H, I) -> Unit
    ) = selectSubscribe(
        this@MvRxView,
        prop1,
        prop2,
        prop3,
        prop4,
        prop5,
        prop6,
        prop7,
        prop8,
        prop9,
        subscriber
    )
}