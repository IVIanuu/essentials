/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.fold
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.io.*
import kotlin.coroutines.*

interface Serializer<T> {
  val defaultData: T

  fun deserialize(serializedData: String): T

  fun serialize(data: T): String
}

fun interface CorruptionHandler<out T> {
  fun onCorruption(serializedData: String, error: SerializerException): T
}

val NoopCorruptionHandler = CorruptionHandler { _, e -> throw e }

fun <T> ReplaceDataCorruptionHandler(
  produceReplacement: (String, SerializerException) -> T
): CorruptionHandler<T> =
  CorruptionHandler { serializedData, error -> produceReplacement(serializedData, error) }

class SerializerException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)

fun <T> DiskDataStore(
  coroutineContext: CoroutineContext,
  produceFile: () -> File,
  produceSerializer: () -> Serializer<T>,
  corruptionHandler: CorruptionHandler<T> = NoopCorruptionHandler
): DataStore<T> = DiskDataStoreImpl(
  coroutineContext = coroutineContext,
  produceFile = produceFile,
  produceSerializer = produceSerializer,
  corruptionHandler = corruptionHandler
)

private class DiskDataStoreImpl<T>(
  override val coroutineContext: CoroutineContext,
  produceFile: () -> File,
  produceSerializer: () -> Serializer<T>,
  private val corruptionHandler: CorruptionHandler<T>
) : DataStore<T>, CoroutineScope {
  private val file by lazy(produceFile)
  private val serializer by lazy(produceSerializer)

  private val state = MutableStateFlow<State<T>>(State.Uninitialized)
  override val data: Flow<T> = flow<T> {
    val initialState = state.value
    if (initialState !is State.Data)
      actor.tryAct(Message.ReadData(initialState))

    emitAll(
      state
        .filter {
          it is State.Data || it is State.Final ||
              it !== initialState
        }
        .map {
          when (it) {
            is State.Data -> it.value
            is State.ReadError -> throw it.error
            is State.Final -> throw it.error
            State.Uninitialized -> throw AssertionError()
          }
        }
    )
  }

  private val actor = actor<Message<T>>(capacity = Channel.UNLIMITED) {
    for (message in this) {
      when (message) {
        is Message.UpdateData -> {
          catch {
            val currentData = readData()
            val newData = message.transform(currentData)
            if (newData != currentData) {
              writeData(newData)
              state.value = State.Data(newData)
            }
            newData
          }
            .fold(
              success = { message.newData.complete(it) },
              failure = { message.newData.completeExceptionally(it) }
            )
        }
        is Message.ReadData -> {
          if (message.lastState is State.Uninitialized) {
            state.value = catch { readData() }
              .fold({ State.Data(it) }, { State.ReadError(it) })
          }
        }
      }
    }
  }

  private sealed interface State<out T> {
    data class Data<T>(val value: T) : State<T>
    data class ReadError<T>(val error: Throwable) : State<T>
    data class Final<T>(val error: Throwable) : State<T>
    object Uninitialized : State<Nothing>
  }

  private sealed interface Message<out T> {
    data class UpdateData<T>(
      val transform: (T) -> T,
      val newData: CompletableDeferred<T>
    ) : Message<T>
    data class ReadData<T>(val lastState: State<T>) : Message<T>
  }

  override suspend fun updateData(transform: (t: T) -> T): T {
    val deferred = CompletableDeferred<T>()
    actor.tryAct(Message.UpdateData(transform, deferred))
    return deferred.await()
  }

  private suspend fun readData(): T {
    if (!file.exists()) return serializer.defaultData

    val serializedData = try {
      file.bufferedReader().use {
        it.readText()
      }
    } catch (t: Throwable) {
      throw IOException("Couldn't read file at '$file'", t)
    }

    return try {
      serializer.deserialize(serializedData)
    } catch (t: SerializerException) {
      val newData = corruptionHandler.onCorruption(serializedData, t)

      try {
        writeData(newData)
      } catch (writeEx: IOException) {
        // If we fail to write the handled data, add the new exception as a suppressed
        // exception.
        t.addSuppressed(writeEx)
        throw t
      }

      // If we reach this point, we've successfully replaced the data on disk with newData.
      newData
    }
  }

  private suspend fun writeData(newData: T) {
    if (!file.exists()) {
      file.parentFile?.mkdirs()

      if (!file.createNewFile())
        throw IOException("Couldn't create '$file'")
    }

    val serializedData = try {
      serializer.serialize(newData)
    } catch (t: SerializerException) {
      throw RuntimeException(
        "Couldn't serialize data '$newData' for file '$file'",
        t
      )
    }

    val tmpFile = File.createTempFile(
      "new", "tmp", file.parentFile
    )

    try {
      tmpFile.bufferedWriter().use { it.write(serializedData) }
      if (!tmpFile.renameTo(file))
        throw IOException("$tmpFile could not be renamed to $file")
    } catch (e: IOException) {
      if (tmpFile.exists())
        tmpFile.delete()
      throw e
    } finally {
      tmpFile.delete()
    }
  }
}
