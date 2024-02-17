/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.activity.ComponentActivity
import arrow.core.Either
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.ScopeManager
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.app.AppVisibleScope
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.RateLimiter
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.scopeOfOrNull
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Provide @Scoped<UiScope> class FullScreenAdManager(
  private val activity: ComponentActivity,
  private val appContext: AppContext,
  private val adsEnabledFlow: Flow<AdsEnabled>,
  private val config: @FinalAdConfig FullScreenAdConfig,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<UiScope>,
  private val scopeManager: ScopeManager
) {
  private val lock = Mutex()
  private var deferredAd: Deferred<suspend () -> Boolean>? = null
  private val rateLimiter = RateLimiter(1, config.adsInterval)

  init {
    scope.launch {
      adsEnabledFlow
        .filter { it.value }
        .collect { loadAd() }
    }
  }

  suspend fun isAdLoaded() = getCurrentAd() != null

  fun preloadAd() {
    scope.launch { loadAd() }
  }

  suspend fun loadAd() = Either.catch {
    if (!adsEnabledFlow.first().value) return@catch false
    getOrCreateCurrentAd()
    true
  }

  suspend fun showAdIfLoaded(): Boolean {
    if (!adsEnabledFlow.first().value) return false
    return (getCurrentAd()?.invoke() ?: false)
      .also { preloadAd() }
  }

  suspend fun loadAndShowAdWithTimeout() = Either.catch {
    if (!adsEnabledFlow.first().value) return@catch false
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
      logger.log { "start loading ad" }

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

      logger.log { "ad loaded" }

      val result: suspend () -> Boolean = {
        if (rateLimiter.tryAcquire()) {
          logger.log { "show ad" }
          lock.withLock { deferredAd = null }
          if (scopeManager.scopeOfOrNull<AppVisibleScope>().first() != null) {
            withContext(coroutineContexts.main) {
              ad.show(activity)
            }
            true
          } else
            false
        } else {
          logger.log { "do not show ad due to rate limit" }
          false
        }
      }

      result
    }.also { deferredAd = it }
  }.await()
}

data class FullScreenAdConfig(val id: String, val adsInterval: Duration = 30.seconds) {
  @Provide companion object {
    @Provide fun final(
      adConfig: FullScreenAdConfig,
      appConfig: AppConfig,
      resources: Resources,
    ): @FinalAdConfig FullScreenAdConfig = if (!appConfig.isDebug) adConfig
    else adConfig.copy(id = resources(R.string.test_ad_unit_id_full_screen))
  }
}

class AdLoadingException(val error: LoadAdError) : RuntimeException()
