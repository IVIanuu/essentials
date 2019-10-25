/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.store

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

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
        channel
            .asFlow()
            .collect { emit(it) }
    }
}