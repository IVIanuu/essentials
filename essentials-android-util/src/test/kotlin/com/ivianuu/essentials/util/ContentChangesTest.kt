/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
  @Test fun testContentChanges() = runCancellingBlockingTest {
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