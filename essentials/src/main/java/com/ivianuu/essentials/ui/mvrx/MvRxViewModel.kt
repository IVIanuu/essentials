package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.ui.common.BaseViewModel
import com.ivianuu.essentials.util.ext.closeBy
import com.ivianuu.ktuples.Nonuple
import com.ivianuu.ktuples.Octuple
import com.ivianuu.ktuples.Quadruple
import com.ivianuu.ktuples.Quintuple
import com.ivianuu.ktuples.Septuple
import com.ivianuu.ktuples.Sextuple
import com.ivianuu.scopes.rx.disposeBy
import com.ivianuu.statestore.StateStore
import com.ivianuu.statestore.rx.observable
import com.ivianuu.timberktx.d
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlin.reflect.KProperty1

/**
 * State view model
 */
abstract class MvRxViewModel<S : MvRxState>(initialState: S) : BaseViewModel() {

    private val stateStore = StateStore(initialState)
        .closeBy(scope)

    internal val state get() = stateStore.peekState()

    protected fun withState(block: (S) -> Unit) {
        stateStore.withState(block)
    }

    protected fun setState(reducer: S.() -> S) {
        stateStore.setState(reducer)
    }

    fun logStateChanges() {
        subscribe { d { "new state -> $it" } }
    }

    protected fun subscribe(subscriber: (S) -> Unit) =
        stateStore.observable.subscribeLifecycle(null, subscriber)

    fun subscribe(owner: LifecycleOwner, subscriber: (S) -> Unit) =
        stateStore.observable.subscribeLifecycle(owner, subscriber)

    protected fun <A> selectSubscribe(
        prop1: KProperty1<S, A>,
        subscriber: (A) -> Unit
    ) = selectSubscribeInternal(null, prop1, subscriber)

    fun <A> selectSubscribe(
        owner: LifecycleOwner,
        prop1: KProperty1<S, A>,
        subscriber: (A) -> Unit
    ) = selectSubscribeInternal(owner, prop1, subscriber)

    private fun <A> selectSubscribeInternal(
        owner: LifecycleOwner?,
        prop1: KProperty1<S, A>,
        subscriber: (A) -> Unit
    ) = stateStore.observable
        .map { prop1.get(it) }
        .distinctUntilChanged()
        .subscribeLifecycle(owner) { a -> subscriber(a) }

    protected fun <A, B> selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        subscriber: (A, B) -> Unit
    ) = selectSubscribeInternal(null, prop1, prop2, subscriber)

    fun <A, B> selectSubscribe(
        owner: LifecycleOwner,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        subscriber: (A, B) -> Unit
    ) = selectSubscribeInternal(owner, prop1, prop2, subscriber)

    private fun <A, B> selectSubscribeInternal(
        owner: LifecycleOwner?,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        subscriber: (A, B) -> Unit
    ) = stateStore.observable
        .map { Pair(prop1.get(it), prop2.get(it)) }
        .distinctUntilChanged()
        .subscribeLifecycle(owner) { (a, b) -> subscriber(a, b) }

    protected fun <A, B, C> selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        subscriber: (A, B, C) -> Unit
    ) = selectSubscribeInternal(null, prop1, prop2, prop3, subscriber)

    fun <A, B, C> selectSubscribe(
        owner: LifecycleOwner,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        subscriber: (A, B, C) -> Unit
    ) = selectSubscribeInternal(owner, prop1, prop2, prop3, subscriber)

    private fun <A, B, C> selectSubscribeInternal(
        owner: LifecycleOwner?,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        subscriber: (A, B, C) -> Unit
    ) = stateStore.observable
        .map { Triple(prop1.get(it), prop2.get(it), prop3.get(it)) }
        .distinctUntilChanged()
        .subscribeLifecycle(owner) { (a, b, c) -> subscriber(a, b, c) }

    protected fun <A, B, C, D> selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        subscriber: (A, B, C, D) -> Unit
    ) = selectSubscribeInternal(null, prop1, prop2, prop3, prop4, subscriber)

    fun <A, B, C, D> selectSubscribe(
        owner: LifecycleOwner,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        subscriber: (A, B, C, D) -> Unit
    ) = selectSubscribeInternal(owner, prop1, prop2, prop3, prop4, subscriber)

    private fun <A, B, C, D> selectSubscribeInternal(
        owner: LifecycleOwner?,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        subscriber: (A, B, C, D) -> Unit
    ) = stateStore.observable
        .map {
            Quadruple(
                prop1.get(it),
                prop2.get(it),
                prop3.get(it),
                prop4.get(it)
            )
        }
        .distinctUntilChanged()
        .subscribeLifecycle(owner) { (a, b, c, d) -> subscriber(a, b, c, d) }

    protected fun <A, B, C, D, E> selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        subscriber: (A, B, C, D, E) -> Unit
    ) = selectSubscribeInternal(null, prop1, prop2, prop3, prop4, prop5, subscriber)

    fun <A, B, C, D, E> selectSubscribe(
        owner: LifecycleOwner,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        subscriber: (A, B, C, D, E) -> Unit
    ) = selectSubscribeInternal(owner, prop1, prop2, prop3, prop4, prop5, subscriber)

    private fun <A, B, C, D, E> selectSubscribeInternal(
        owner: LifecycleOwner?,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        subscriber: (A, B, C, D, E) -> Unit
    ) = stateStore.observable
        .map {
            Quintuple(
                prop1.get(it),
                prop2.get(it),
                prop3.get(it),
                prop4.get(it),
                prop5.get(it)
            )
        }
        .distinctUntilChanged()
        .subscribeLifecycle(owner) { (a, b, c, d, e) -> subscriber(a, b, c, d, e) }

    protected fun <A, B, C, D, E, F> selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        subscriber: (A, B, C, D, E, F) -> Unit
    ) = selectSubscribeInternal(null, prop1, prop2, prop3, prop4, prop5, prop6, subscriber)

    fun <A, B, C, D, E, F> selectSubscribe(
        owner: LifecycleOwner,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        subscriber: (A, B, C, D, E, F) -> Unit
    ) = selectSubscribeInternal(owner, prop1, prop2, prop3, prop4, prop5, prop6, subscriber)

    private fun <A, B, C, D, E, F> selectSubscribeInternal(
        owner: LifecycleOwner?,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        subscriber: (A, B, C, D, E, F) -> Unit
    ) = stateStore.observable
        .map {
            Sextuple(
                prop1.get(it),
                prop2.get(it),
                prop3.get(it),
                prop4.get(it),
                prop5.get(it),
                prop6.get(it)
            )
        }
        .distinctUntilChanged()
        .subscribeLifecycle(owner) { (a, b, c, d, e, f) -> subscriber(a, b, c, d, e, f) }

    protected fun <A, B, C, D, E, F, G> selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        prop7: KProperty1<S, G>,
        subscriber: (A, B, C, D, E, F, G) -> Unit
    ) = selectSubscribeInternal(null, prop1, prop2, prop3, prop4, prop5, prop6, prop7, subscriber)

    fun <A, B, C, D, E, F, G> selectSubscribe(
        owner: LifecycleOwner,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        prop7: KProperty1<S, G>,
        subscriber: (A, B, C, D, E, F, G) -> Unit
    ) = selectSubscribeInternal(owner, prop1, prop2, prop3, prop4, prop5, prop6, prop7, subscriber)

    private fun <A, B, C, D, E, F, G> selectSubscribeInternal(
        owner: LifecycleOwner?,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        prop7: KProperty1<S, G>,
        subscriber: (A, B, C, D, E, F, G) -> Unit
    ) = stateStore.observable
        .map {
            Septuple(
                prop1.get(it),
                prop2.get(it),
                prop3.get(it),
                prop4.get(it),
                prop5.get(it),
                prop6.get(it),
                prop7.get(it)
            )
        }
        .distinctUntilChanged()
        .subscribeLifecycle(owner) { (a, b, c, d, e, f, g) -> subscriber(a, b, c, d, e, f, g) }

    protected fun <A, B, C, D, E, F, G, H> selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        prop7: KProperty1<S, G>,
        prop8: KProperty1<S, H>,
        subscriber: (A, B, C, D, E, F, G, H) -> Unit
    ) = selectSubscribeInternal(
        null,
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

    fun <A, B, C, D, E, F, G, H> selectSubscribe(
        owner: LifecycleOwner,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        prop7: KProperty1<S, G>,
        prop8: KProperty1<S, H>,
        subscriber: (A, B, C, D, E, F, G, H) -> Unit
    ) = selectSubscribeInternal(
        owner,
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

    private fun <A, B, C, D, E, F, G, H> selectSubscribeInternal(
        owner: LifecycleOwner?,
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        prop4: KProperty1<S, D>,
        prop5: KProperty1<S, E>,
        prop6: KProperty1<S, F>,
        prop7: KProperty1<S, G>,
        prop8: KProperty1<S, H>,
        subscriber: (A, B, C, D, E, F, G, H) -> Unit
    ) = stateStore.observable
        .map {
            Octuple(
                prop1.get(it),
                prop2.get(it),
                prop3.get(it),
                prop4.get(it),
                prop5.get(it),
                prop6.get(it),
                prop7.get(it),
                prop8.get(it)
            )
        }
        .distinctUntilChanged()
        .subscribeLifecycle(owner) { (a, b, c, d, e, f, g, h) ->
            subscriber(
                a,
                b,
                c,
                d,
                e,
                f,
                g,
                h
            )
        }

    protected fun <A, B, C, D, E, F, G, H, I> selectSubscribe(
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
    ) = selectSubscribeInternal(
        null,
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

    fun <A, B, C, D, E, F, G, H, I> selectSubscribe(
        owner: LifecycleOwner,
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
    ) = selectSubscribeInternal(
        owner,
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

    private fun <A, B, C, D, E, F, G, H, I> selectSubscribeInternal(
        owner: LifecycleOwner?,
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
    ) = stateStore.observable
        .map {
            Nonuple(
                prop1.get(it),
                prop2.get(it),
                prop3.get(it),
                prop4.get(it),
                prop5.get(it),
                prop6.get(it),
                prop7.get(it),
                prop8.get(it),
                prop9.get(it)
            )
        }
        .distinctUntilChanged()
        .subscribeLifecycle(owner) { (a, b, c, d, e, f, g, h, i) ->
            subscriber(
                a,
                b,
                c,
                d,
                e,
                f,
                g,
                h,
                i
            )
        }

    private fun <T> Observable<T>.subscribeLifecycle(
        owner: LifecycleOwner? = null,
        subscriber: (T) -> Unit
    ): Disposable {
        if (owner == null) {
            return observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber)
                .disposeBy(scope)
        }

        val lifecycleAwareObserver = LifecycleAwareObserver(
            owner,
            alwaysDeliverLastValueWhenUnlocked = true,
            onNext = subscriber
        )
        return observeOn(AndroidSchedulers.mainThread()).subscribeWith(lifecycleAwareObserver)
            .disposeBy(scope)
    }

    override fun toString() = "${this::class.java.simpleName} -> $state"
}