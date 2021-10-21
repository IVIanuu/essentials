/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
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
  block: suspend ActorScope<T>.() -> Unit
): Actor<T> = ActorImpl(coroutineContext + context, capacity, start, block)

interface ActorScope<T> : CoroutineScope, ReceiveChannel<T>

private class ActorImpl<T>(
  coroutineContext: CoroutineContext,
  capacity: Int,
  start: CoroutineStart,
  block: suspend ActorScope<T>.() -> Unit,
  private val mailbox: Channel<T> = Channel(capacity = capacity)
) : Actor<T>, ActorScope<T>, ReceiveChannel<T> by mailbox, CoroutineScope {
  override val coroutineContext: CoroutineContext =
    coroutineContext + coroutineContext[Job]!!.childJob()

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

fun CoroutineScope.actor(
  context: CoroutineContext = this.coroutineContext,
  capacity: Int = 64
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
