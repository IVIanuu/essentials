/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.ContentResolver
import android.database.ContentObserver
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollectIn
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test
import org.junit.runner.RunWith

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
    )(mockk()).testCollectIn(this)

    observer.onChange(false)
    observer.onChange(false)
    observer.onChange(false)

    collector.values shouldHaveSize 3
  }
}