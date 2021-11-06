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

package com.ivianuu.essentials.app

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf
import io.kotest.matchers.collections.shouldContainInOrder
import org.junit.Test

class LoadingOrderTest {
  @Test fun testAfter() {
    val unsorted = listOf(
      Item(
        key = cKey,
        loadingOrder = LoadingOrder<Item>()
          .after<B>()
      ),
      Item(
        key = bKey,
        loadingOrder = LoadingOrder<Item>()
          .after<A>()
      ),
      Item(key = aKey)
    )

    val sorted = unsorted.sortedWithLoadingOrder()

    sorted
      .map { it.key }
      .shouldContainInOrder(aKey, bKey, cKey)
  }

  @Test fun testBefore() {
    val unsorted = listOf(
      Item(key = cKey),
      Item(
        key = bKey,
        loadingOrder = LoadingOrder<Item>()
          .before<C>()
      ),
      Item(
        key = aKey,
        loadingOrder = LoadingOrder<Item>()
          .before<B>()
      )
    )

    val sorted = unsorted.sortedWithLoadingOrder()

    sorted
      .map { it.key }
      .shouldContainInOrder(aKey, bKey, cKey)
  }

  @Test fun testFirst() {
    val unsorted = listOf(
      Item(key = bKey),
      Item(key = cKey),
      Item(
        key = aKey,
        loadingOrder = LoadingOrder<Item>().first()
      )
    )

    val sorted = unsorted.sortedWithLoadingOrder()

    sorted
      .map { it.key }
      .shouldContainInOrder(aKey, bKey, cKey)
  }

  @Test fun testLast() {
    val unsorted = listOf(
      Item(
        key = cKey,
        loadingOrder = LoadingOrder<Item>().last()
      ),
      Item(key = aKey),
      Item(key = bKey)
    )

    val sorted = unsorted.sortedWithLoadingOrder()

    sorted
      .map { it.key }
      .shouldContainInOrder(aKey, bKey, cKey)
  }

  @Test fun testPreserversOrderForNone() {
    val unsorted = listOf(
      Item(key = aKey),
      Item(key = bKey),
      Item(key = cKey)
    )

    val sorted = unsorted.sortedWithLoadingOrder()

    sorted
      .map { it.key }
      .shouldContainInOrder(aKey, bKey, cKey)
  }
}

private val aKey = typeKeyOf<A>()
private val bKey = typeKeyOf<B>()
private val cKey = typeKeyOf<C>()

@Tag private annotation class ATag
private typealias A = @ATag Item
@Tag private annotation class BTag
private typealias B = @BTag Item
@Tag private annotation class CTag
private typealias C = @CTag Item

private data class Item(
  val key: TypeKey<*>,
  val loadingOrder: LoadingOrder<Item> = LoadingOrder()
) {
  companion object {
    @Provide val descriptor = object : LoadingOrder.Descriptor<Item> {
      override fun key(item: Item) = item.key
      override fun loadingOrder(item: Item) = item.loadingOrder
    }
  }
}
