package com.ivianuu.essentials.ui.mvrx

import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach
import java.util.*

/**
 * Mvrx state store
 */
internal class MvRxStateStore<S : Any>(coroutineDispatcher: CoroutineDispatcher = DefaultDispatcher) {

    private val stateChannel = ConflatedBroadcastChannel<S>()

    val channel = stateChannel.openSubscription()

    internal val state: S
        get() {
            requireInitialState()
            return stateChannel.value
        }

    private val hasInitialState get() = stateChannel.valueOrNull != null

    private val actor =
        actor<Message<S>>(context = coroutineDispatcher, capacity = Channel.UNLIMITED) {
            val getQueue = LinkedList<(S) -> Unit>()

            channel.consumeEach { msg ->
                try {
                    when (msg) {
                        is Message.GetQueueElement<S> -> getQueue.push(msg.block)

                        is Message.SetQueueElement<S> -> stateChannel.value
                            .let { msg.reducer(it) }
                            .let { stateChannel.offer(it) }
                    }

                    if (channel.isEmpty) {
                        getQueue.forEach { it(stateChannel.value) }
                        getQueue.clear()
                    }
                } catch (t: Throwable) {
                    handleError(t)
                }
            }
    }

    fun setInitialState(initialState: S) {
        if (hasInitialState) throw IllegalStateException("initial state already set")
        stateChannel.offer(initialState)
    }

    fun get(block: (S) -> Unit) {
        requireInitialState()
        actor.offer(Message.GetQueueElement(block))
    }

    fun set(stateReducer: S.() -> S) {
        requireInitialState()
        actor.offer(Message.SetQueueElement(stateReducer))
    }

    private fun handleError(throwable: Throwable) {
        // Throw the root cause to remove all of the rx stacks.
        // TODO: better error handling
        var e: Throwable? = throwable
        while (e?.cause != null) e = e.cause
        e?.let { throw it }
    }

    private fun requireInitialState() {
        if (!hasInitialState) throw IllegalStateException("set initial state must be called first")
    }

    private sealed class Message<S> {
        class SetQueueElement<S>(val reducer: S.() -> S) : Message<S>()
        class GetQueueElement<S>(val block: (S) -> Unit) : Message<S>()
    }
}