package com.ivianuu.essentials

import injekt.*
import io.kotest.matchers.collections.*
import org.junit.*

private val aKey = typeKeyOf<A>()
private val bKey = typeKeyOf<B>()
private val cKey = typeKeyOf<C>()

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
private annotation class ATag
private typealias A = @ATag Item
@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
private annotation class BTag
private typealias B = @BTag Item
@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
private annotation class CTag
private typealias C = @CTag Item

private data class Item(
  val key: TypeKey<*>,
  val loadingOrder: LoadingOrder<Item> = LoadingOrder()
) {
  @Provide companion object {
    @Provide val descriptor = object : LoadingOrder.Descriptor<Item> {
      override fun key(item: Item) = item.key
      override fun loadingOrder(item: Item) = item.loadingOrder
    }
  }
}

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

  @Test fun testAfterLast() {
    val unsorted = listOf(
      Item(key = aKey, loadingOrder = LoadingOrder<Item>().last()),
      Item(key = bKey, loadingOrder = LoadingOrder<Item>().after(aKey))
    )

    val sorted = unsorted.sortedWithLoadingOrder()

    sorted
      .map { it.key }
      .shouldContainInOrder(aKey, bKey)
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