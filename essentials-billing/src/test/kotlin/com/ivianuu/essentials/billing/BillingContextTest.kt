package com.ivianuu.essentials.billing

import androidx.test.ext.junit.runners.*
import com.android.billingclient.api.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.booleans.*
import io.mockk.*
import kotlinx.coroutines.flow.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class BillingContextTest {
  @Test fun testWithConnection() = runCancellingBlockingTest {
    val context = BillingContextImpl(
      billingClient = mockk {
        every { startConnection(any()) } answers {
          arg<BillingClientStateListener>(0)
            .onBillingSetupFinished(
              BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.OK)
                .build()
            )
        }
      },
      dispatcher = dispatcher,
      logger = com.ivianuu.essentials.logging.NoopLogger,
      refreshes = MutableSharedFlow(),
      scope = this
    )
    var ran = false
    context.withConnection { ran = true }
    ran.shouldBeTrue()
  }
}
