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

package com.ivianuu.essentials.util

import android.content.*
import android.database.*
import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.collections.*
import io.mockk.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ContentChangesTest {
  @Test
  fun testContentChanges() = runCancellingBlockingTest {
    lateinit var observer: ContentObserver
    val contentResolver = mockk<ContentResolver> {
      every { registerContentObserver(any(), any(), any()) } answers {
        observer = arg(2)
      }
    }
    val dispatcher = TestCoroutineDispatcher()
    val collector = contentChangesFactory(
      contentResolver,
      dispatcher
    )(mockk()).testCollect(this)

    observer.onChange(false)
    observer.onChange(false)
    observer.onChange(false)

    collector.values shouldHaveSize 3
  }
}