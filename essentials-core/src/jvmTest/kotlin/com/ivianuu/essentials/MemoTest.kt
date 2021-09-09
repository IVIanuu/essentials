package com.ivianuu.essentials

import com.ivianuu.injekt.scope.Disposable
import com.ivianuu.injekt.scope.withScope
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.Test

class MemoTest {
  @Test fun testMemo() {
    withScope {
      val a = memo("key", "arg") { "a" }
      a shouldBe a
      val b = memo("key", "arg") { "b" }
      a shouldBe b
      val c = memo("key", "other_arg") { "c" }
      c shouldBe "c"
    }
  }

  @Test fun testMemoDisposesValueOnScopeDisposable() {
    val disposable = TestDisposable()
    withScope {
      disposable.isDisposed.shouldBeFalse()
      memo("key") { disposable }
      disposable.isDisposed.shouldBeFalse()
    }
    disposable.isDisposed.shouldBeTrue()
  }

  @Test fun testMemoDisposesValueOnArgChanges() {
    withScope {
      val disposable = TestDisposable()
      memo("a", "arg") { disposable }
      disposable.isDisposed.shouldBeFalse()
      memo("a", "other_arg") { TestDisposable() }
      disposable.isDisposed.shouldBeTrue()
    }
  }

  class TestDisposable : Disposable {
    var isDisposed = false
    override fun dispose() {
      isDisposed = true
    }
  }
}
