package essentials.sample

import com.android.billingclient.api.*
import essentials.premium.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide object DummyPremiumVersionManager : PremiumVersionManager {
  override val isPremiumVersion = MutableStateFlow(true)

  override suspend fun getPremiumSkuDetails(): SkuDetails =
    awaitCancellation()

  override suspend fun consumePremiumVersion(): Boolean = false

  override suspend fun purchasePremiumVersion(): Boolean = false

  override suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? = block()
}
