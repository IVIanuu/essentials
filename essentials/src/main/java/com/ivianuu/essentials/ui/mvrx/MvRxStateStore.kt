package com.ivianuu.essentials.ui.mvrx

import com.ivianuu.essentials.util.ext.requireValue
import com.ivianuu.rxjavaktx.BehaviorSubject
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * State store
 */
internal class MvRxStateStore<S : MvRxState>(initialState: S) : Disposable {

    private val disposables = CompositeDisposable()

    private val subject = BehaviorSubject(initialState)

    private val flushQueueSubject = BehaviorSubject<Unit>()

    private val jobs = Jobs<S>()

    val observable: Observable<S> = subject.distinctUntilChanged()

    internal val state: S
        get() = subject.requireValue()

    init {
        flushQueueSubject
            .observeOn(Schedulers.newThread())
            // We don't want race conditions with setting the state on multiple background threads
            // simultaneously in which two state reducers get the same initial state to reduce.
            .subscribeBy(
                onNext = { flushQueues() },
                onError = { handleError(it) }
            )
            .addTo(disposables)
    }

    fun get(block: (S) -> Unit) {
        jobs.enqueueGetStateBlock(block)
        flushQueueSubject.onNext(Unit)
    }

    fun set(stateReducer: S.() -> S) {
        jobs.enqueueSetStateBlock(stateReducer)
        flushQueueSubject.onNext(Unit)
    }

    private fun flushQueues() {
        flushSetStateQueue()
        val block = jobs.dequeueGetStateBlock() ?: return
        block(state)
        flushQueues()
    }

    private fun flushSetStateQueue() {
        val blocks = jobs.dequeueAllSetStateBlocks() ?: return

        blocks
            .fold(state) { state, reducer -> state.reducer() }
            .run { subject.onNext(this) }
    }

    private fun handleError(throwable: Throwable) {
        // Throw the root cause to remove all of the rx stacks.
        // TODO: better error handling
        var e: Throwable? = throwable
        while (e?.cause != null) e = e.cause
        e?.let { throw it }
    }

    private class Jobs<S> {

        private val getStateQueue = LinkedList<(state: S) -> Unit>()
        private var setStateQueue = LinkedList<S.() -> S>()

        @Synchronized
        fun enqueueGetStateBlock(block: (state: S) -> Unit) {
            getStateQueue.add(block)
        }

        @Synchronized
        fun enqueueSetStateBlock(block: S.() -> S) {
            setStateQueue.add(block)
        }

        @Synchronized
        fun dequeueGetStateBlock(): ((state: S) -> Unit)? {
            if (getStateQueue.isEmpty()) return null

            return getStateQueue.removeFirst()
        }

        @Synchronized
        fun dequeueAllSetStateBlocks(): List<(S.() -> S)>? {
            // do not allocate empty queue for no-op flushes
            if (setStateQueue.isEmpty()) return null

            val queue = setStateQueue
            setStateQueue = LinkedList()
            return queue
        }
    }

    override fun isDisposed() = disposables.isDisposed

    override fun dispose() {
        disposables.dispose()
    }
}