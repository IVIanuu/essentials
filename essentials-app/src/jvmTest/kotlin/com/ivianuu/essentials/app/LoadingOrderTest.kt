package com.ivianuu.essentials.app

import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import io.kotest.matchers.collections.*
import org.junit.*

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

private val aKey = inject<TypeKey<A>>()
private val bKey = inject<TypeKey<B>>()
private val cKey = inject<TypeKey<C>>()

private typealias A = Item
private typealias B = Item
private typealias C = Item

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
