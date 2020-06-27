package com.ivianuu.essentials.store

import kotlinx.coroutines.CoroutineScope
import java.io.File

class DiskDataStoreFactory(
    private val scope: CoroutineScope,
    private val produceBoxDirectory: () -> File,
    @PublishedApi internal val serializerFactory: MoshiSerializerFactory
) {

    private val boxDirectory by lazy(produceBoxDirectory)

    inline fun <reified T> create(
        name: String,
        noinline produceDefaultData: () -> T
    ): DataStore<T> = create(
        name = name,
        produceSerializer = { serializerFactory.create() },
        produceDefaultData = produceDefaultData
    )

    fun <T> create(
        name: String,
        produceSerializer: () -> Serializer<T>,
        produceDefaultData: () -> T
    ): DataStore<T> {
        return DiskDataStore(
            produceFile = { File(boxDirectory, name) },
            produceSerializer = produceSerializer,
            produceDefaultData = produceDefaultData,
            scope = scope
        )
    }

}
