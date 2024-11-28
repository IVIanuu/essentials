/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.activity.*
import androidx.compose.runtime.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlin.coroutines.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

@Provide @Scoped<UiScope> class FullScreenAdManager(
  private val activity: ComponentActivity,
  private val appContext: AppContext,
  private val appScope: Scope<AppScope>,
  private val adsEnabledState: State<AdsEnabled>,
  private val config: @FinalAdConfig FullScreenAdConfig,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<UiScope>
) {
  private val lock = Mutex()
  private var deferredAd: Deferred<suspend () -> Boolean>? = null
  private val rateLimiter = RateLimiter(1, config.adsInterval)

  init {
    scope.launch {
      snapshotFlow { adsEnabledState.value }
        .filter { it.value }
        .collect { loadAd() }
    }
  }

  suspend fun isAdLoaded() = getCurrentAd() != null

  fun preloadAd() {
    scope.launch { loadAd() }
  }

  suspend fun loadAd() = catch {
    if (!adsEnabledState.value.value) return@catch false
    getOrCreateCurrentAd()
    true
  }

  suspend fun showAdIfLoaded(): Boolean {
    if (!adsEnabledState.value.value) return false
    return (getCurrentAd()?.invoke() ?: false)
      .also { preloadAd() }
  }

  suspend fun loadAndShowAdWithTimeout() = catch {
    if (!adsEnabledState.value.value) return@catch false
    withTimeoutOrNull(1.seconds) { getOrCreateCurrentAd() }?.invoke() == true
  }

  private suspend fun getCurrentAd(): (suspend () -> Boolean)? = lock.withLock {
    deferredAd?.takeUnless { it.isCompleted && it.getCompletionExceptionOrNull() != null }
      ?.await()
  }

  private suspend fun getOrCreateCurrentAd(): suspend () -> Boolean = lock.withLock {
    deferredAd?.takeUnless {
      it.isCompleted && it.getCompletionExceptionOrNull() != null
    } ?: scope.async(coroutineContexts.main) {
      logger.d { "start loading ad" }

      val ad = suspendCoroutine { cont ->
        InterstitialAd.load(
          appContext,
          config.id,
          AdRequest.Builder().build(),
          object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
              cont.resume(ad)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
              cont.resumeWithException(AdLoadingException(error))
            }
          }
        )
      }

      logger.d { "ad loaded" }

      val result: suspend () -> Boolean = {
        if (appScope.scopeOfOrNull<AppVisibleScope>() != null) {
          if (rateLimiter.tryAcquire()) {
            logger.d { "show ad" }
            lock.withLock { deferredAd = null }
            withContext(coroutineContexts.main) {
              ad.show(activity)
            }
            true
          } else {
            logger.d { "do not show ad due to rate limit" }
            false
          }
        } else false
      }

      result
    }.also { deferredAd = it }
  }.await()
}

data class FullScreenAdConfig(val id: String, val adsInterval: Duration = 30.seconds) {
  @Provide companion object {
    @Provide fun final(
      adConfig: FullScreenAdConfig,
      appConfig: AppConfig
    ): @FinalAdConfig FullScreenAdConfig = if (!appConfig.isDebug) adConfig
    else adConfig.copy(id = "ca-app-pub-3940256099942544/1033173712")
  }
}

class AdLoadingException(val error: LoadAdError) : RuntimeException()
