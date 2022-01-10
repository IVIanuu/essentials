/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlin.coroutines.*

interface Actor<T> : CoroutineScope {
  suspend fun act(message: T)

  fun tryAct(message: T): ChannelResult<Unit>
}

fun <T> actor(
  context: CoroutineContext = EmptyCoroutineContext,
  capacity: Int = 64,
  start: CoroutineStart = CoroutineStart.LAZY,
  @Inject scope: CoroutineScope,
  block: suspend ActorScope<T>.() -> Unit
): Actor<T> = ActorImpl(scope.coroutineContext + context, capacity, start, block)

interface ActorScope<T> : CoroutineScope, ReceiveChannel<T>

private class ActorImpl<T>(
  coroutineContext: CoroutineContext,
  capacity: Int,
  start: CoroutineStart,
  block: suspend ActorScope<T>.() -> Unit,
  private val mailbox: Channel<T> = Channel(capacity = capacity)
) : Actor<T>, ActorScope<T>, ReceiveChannel<T> by mailbox, CoroutineScope {
  override val coroutineContext: CoroutineContext = coroutineContext.childCoroutineContext()

  private val job = launch(start = start) { block() }

  override suspend fun act(message: T) {
    job.start()
    mailbox.send(message)
  }

  override fun tryAct(message: T): ChannelResult<Unit> {
    job.start()
    return mailbox.trySend(message)
  }
}

fun actor(
  context: CoroutineContext = EmptyCoroutineContext,
  capacity: Int = 64,
  @Inject S: CoroutineScope
): Actor<suspend () -> Unit> = actor(context, capacity) {
  for (block in this) block()
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
