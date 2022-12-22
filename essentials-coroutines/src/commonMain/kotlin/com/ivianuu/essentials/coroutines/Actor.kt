/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface Actor<T> : CoroutineScope {
  suspend fun act(message: T)

  fun tryAct(message: T): ChannelResult<Unit>
}

fun <T> CoroutineScope.actor(
  context: CoroutineContext = EmptyCoroutineContext,
  capacity: Int = 64,
  start: CoroutineStart = CoroutineStart.LAZY,
  block: suspend context(ActorScope<T>) () -> Unit
): Actor<T> = ActorImpl(coroutineContext + context, capacity, start, block)

interface ActorScope<T> : CoroutineScope {
  val messages: ReceiveChannel<T>
}

private class ActorImpl<T>(
  coroutineContext: CoroutineContext,
  capacity: Int,
  start: CoroutineStart,
  block: suspend context(ActorScope<T>) () -> Unit,
  override val messages: Channel<T> = Channel(capacity = capacity)
) : Actor<T>, ActorScope<T> {
  override val coroutineContext: CoroutineContext = coroutineContext.childCoroutineContext()

  private val job = launch(start = start) { block() }

  override suspend fun act(message: T) {
    job.start()
    messages.send(message)
  }

  override fun tryAct(message: T): ChannelResult<Unit> {
    job.start()
    return messages.trySend(message)
  }
}

fun CoroutineScope.actor(
  context: CoroutineContext = EmptyCoroutineContext,
  capacity: Int = 64
): Actor<suspend () -> Unit> = actor(context, capacity) {
  for (block in messages) block()
}

suspend fun <T> Actor<suspend () -> Unit>.actAndReply(block: suspend () -> T): T {
  val reply = CompletableDeferred<T>()
  act {
    try {
      reply.complete(block())
    } catch (e: Throwable) {
      reply.completeExceptionally(e)
    }
  }
  return reply.await()
}
