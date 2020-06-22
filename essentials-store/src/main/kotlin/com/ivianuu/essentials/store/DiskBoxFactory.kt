package com.ivianuu.essentials.store

import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class DiskBoxFactory(
    private val scope: CoroutineScope,
    private val produceBoxDirectory: () -> File,
    @PublishedApi internal val serializerFactory: MoshiSerializerFactory
) {

    private val boxes = ConcurrentHashMap<String, Box<*>>()

    private val boxDirectory by lazy(produceBoxDirectory)

    inline fun <reified T> create(
        name: String,
        noinline produceDefaultData: () -> T
    ): Box<T> = create(
        name = name,
        produceSerializer = { serializerFactory.create() },
        produceDefaultData = produceDefaultData
    )

    fun <T> create(
        name: String,
        produceSerializer: () -> Serializer<T>,
        produceDefaultData: () -> T
    ): Box<T> {
        var box = boxes[name]
        if (box == null) {
            box = DiskBox(
                produceFile = { File(boxDirectory, name) },
                produceSerializer = produceSerializer,
                produceDefaultData = produceDefaultData,
                scope = scope
            )
            boxes[name] = box
        }

        return box as Box<T>
    }

}
