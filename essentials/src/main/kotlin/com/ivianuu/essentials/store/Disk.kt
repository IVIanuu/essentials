package com.ivianuu.essentials.store

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import java.io.*

interface DiskBox<T> : Box<T> {

    interface Serializer<T> {
        fun deserialize(serialized: String): T
        fun serialize(value: T): String
    }

}

internal class DiskBoxImpl<T>(
    private val file: File,
    private val serializer: DiskBox.Serializer<T>
) : DiskBox<T> {

    private val channel = BroadcastChannel<T>(1)

    init {
        check(!file.isDirectory)
    }

    override suspend fun set(value: T) {
        val serialized = serializer.serialize(value)

        val tmpFile = File.createTempFile(
            "new", "tmp", file.parentFile
        )
        try {
            BufferedOutputStream(FileOutputStream(tmpFile)).run {
                write(serialized.toByteArray())
                flush()
                close()
            }
            if (!tmpFile.renameTo(file)) {
                throw IOException("Couldn't move tmp file to file $file")
            }
        } catch (e: Exception) {
            throw IOException("Couldn't write to file $file $serialized", e)
        }

        channel.offer(value)
    }

    override suspend fun get(): T {
        try {
            val reader = BufferedReader(InputStreamReader(FileInputStream(file)))
            val value = StringBuilder()
            var line = reader.readLine()
            while (line != null) {
                value.append(line).append('\n')
                line = reader.readLine()
            }

            reader.close()
            return serializer.deserialize(value.toString())
        } catch (e: Exception) {
            throw IOException("Couldn't read file $file")
        }
    }

    override suspend fun delete() {
        if (file.exists()) {
            if (!file.delete()) {
                throw IOException("Couldn't delete file $file")
            }
            return
        }
    }

    override suspend fun exists(): Boolean = file.exists()

    override fun asFlow(): Flow<T> = flow {
        emit(get())
        channel.openSubscription()
            .consumeAsFlow()
            .collect { emit(it) }
    }
}