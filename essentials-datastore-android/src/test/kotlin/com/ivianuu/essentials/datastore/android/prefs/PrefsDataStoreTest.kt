/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.datastore.android.prefs

import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.MoshiSerializerFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import strikt.assertions.isTrue
import java.nio.file.Files

class PrefsDataStoreTest {

    private val scope = TestCoroutineScope()
    private val prefsDir = Files.createTempDirectory("dir").toFile()
    private val moshi = Moshi.Builder().build()
    private val diskDataStoreFactory = DiskDataStoreFactory(
        scope,
        { prefsDir },
        { MoshiSerializerFactory(moshi) }
    )

    var store = createStore()
    private fun createStore() = prefsDataStore(diskDataStoreFactory) { PrefsSerializer(moshi) }

    private val countKey = prefKeyOf<Int>("count")

    @Test
    fun testInitialIsEmpty() = scope.runBlockingTest {
        expectThat(store.data.first())
            .isEqualTo(mutablePrefsOf())
    }

    @Test
    fun testGetOrNull() = scope.runBlockingTest {
        expectThat(store.data.first().getOrNull(countKey)).isNull()
        store.edit { this[countKey] = 0 }
        expectThat(store.data.first().getOrNull(countKey)).isEqualTo(0)
    }

    @Test(expected = IllegalStateException::class)
    fun testGetWithMissingValue() = scope.runBlockingTest {
        store.data.first()[countKey]
    }

    @Test
    fun testGetWithExistingValue() = scope.runBlockingTest {
        store.edit { this[countKey] = 0 }
        expectThat(store.data.first()[countKey]).isEqualTo(0)
    }

    @Test
    fun testGetOrElse() = scope.runBlockingTest {
        expectThat(store.data.first().getOrElse(countKey) { 0 }).isEqualTo(0)
        store.edit { this[countKey] = 1 }
        expectThat(store.data.first().getOrElse(countKey) { 0 }).isEqualTo(1)
    }

    @Test
    fun testContains() = scope.runBlockingTest {
        expectThat(countKey in store.data.first()).isFalse()
        store.edit { this[countKey] = 0 }
        expectThat(countKey in store.data.first()).isTrue()
    }

    @Test
    fun testSet() = scope.runBlockingTest {
        store.edit { this[countKey] = 0 }
        expectThat(store.data.first()[countKey]).isEqualTo(0)
    }

    @Test
    fun testRemove() = scope.runBlockingTest {
        store.edit {
            this[countKey] = 0
            this -= countKey
        }
        expectThat(store.data.first().getOrNull(countKey)).isNull()
    }

    @Test
    fun testClear() = scope.runBlockingTest {
        store.edit { this[countKey] = 0 }
        expectThat(countKey in store.data.first()).isTrue()
        store.edit { clear() }
        expectThat(countKey in store.data.first()).isFalse()
    }

    @Test
    fun testReadWrite() = scope.runBlockingTest {
        val values = mutableListOf<Int?>()
        val collectorJob = scope.launch {
            store.data.collect { values += it.getOrNull(countKey) }
        }
        store.edit { this[countKey] = 0 }
        store.edit { this[countKey] = this[countKey] + 1 }
        expectThat(values).containsExactly(null, 0, 1)
        collectorJob.cancelAndJoin()
    }

    @Test
    fun testSupportedTypes() {
        prefKeyOf<Boolean>("name")
        prefKeyOf<Double>("name")
        prefKeyOf<Float>("name")
        prefKeyOf<Int>("name")
        prefKeyOf<Long>("name")
        prefKeyOf<String>("name")
        prefKeyOf<Set<String>>("name")
    }

    @Test
    fun testCanLoadDataFromDisk() = scope.runBlockingTest {
        store.edit { this[countKey] = 0 }
        expectThat(store.data.first().getOrNull(countKey)).isEqualTo(0)
        store = createStore()
        expectThat(store.data.first().getOrNull(countKey)).isEqualTo(0)
    }

    @Test(expected = IllegalStateException::class)
    fun testUnsupportedTypes() {
        prefKeyOf<Prefs>("name")
    }

}
