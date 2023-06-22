/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.RateLimiter
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.util.ForegroundActivity
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.MainCoroutineContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration

interface FullScreenAdManager {
  suspend fun isAdLoaded(): Boolean

  fun preloadAd()

  suspend fun loadAd(): Result<Boolean, Throwable>

  suspend fun loadAndShowAd(): Result<Boolean, Throwable>

  suspend fun showAdIfLoaded(): Boolean
}

@JvmInline value class FullScreenAdId(val value: String) {
  companion object {
    @Provide fun default(
      appConfig: AppConfig,
      resources: Resources
    ) = FullScreenAdId(
      resources(
        if (appConfig.isDebug) R.string.es_test_ad_unit_id_interstitial
        else R.string.es_full_screen_ad_unit_id
      )
    )
  }
}

data class FullScreenAdConfig(val adsInterval: Duration) {
  companion object {
    @Provide val defaultConfig get() = FullScreenAdConfig(30.seconds)
  }
}

@Provide @Scoped<UiScope> class FullScreenAdManagerImpl(
  private val appContext: AppContext,
  private val adsEnabledStates: Flow<AdsEnabled>,
  private val id: FullScreenAdId,
  config: FullScreenAdConfig,
  private val foregroundActivities: Flow<ForegroundActivity>,
  private val logger: Logger,
  private val mainCoroutineContext: MainCoroutineContext,
  private val scope: ScopedCoroutineScope<AppScope>
) : FullScreenAdManager {
  private val lock = Mutex()
  private var deferredAd: Deferred<suspend () -> Boolean>? = null
  private val rateLimiter = RateLimiter(1, config.adsInterval)

  override suspend fun isAdLoaded() = getCurrentAd() != null

  override fun preloadAd() {
    scope.launch { loadAd() }
  }

  override suspend fun loadAd() = catch {
    if (!adsEnabledStates.first().value) return@catch false
    getOrCreateCurrentAd()
    true
  }

  override suspend fun loadAndShowAd() = catch {
    if (!adsEnabledStates.first().value) return@catch false
    getOrCreateCurrentAd()
      .also { preloadAd() }
      .invoke()
  }

  override suspend fun showAdIfLoaded() = getCurrentAd()
    ?.let {
      it.invoke()
        .also { preloadAd() }
    } ?: false

  private suspend fun getCurrentAd(): (suspend () -> Boolean)? = lock.withLock {
    deferredAd?.takeUnless { it.isCompleted && it.getCompletionExceptionOrNull() != null }
      ?.await()
  }

  private suspend fun getOrCreateCurrentAd(): suspend () -> Boolean = lock.withLock {
    deferredAd?.takeUnless {
      it.isCompleted && it.getCompletionExceptionOrNull() != null
    } ?: scope.async(mainCoroutineContext) {
      logger.log { "start loading ad" }

      val ad = suspendCoroutine<InterstitialAd> { cont ->
        InterstitialAd.load(
          appContext,
          id.value,
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
          withContext(mainCoroutineContext) {
            ad.show(foregroundActivities.first()!!)
          }
          true
        } else {
          logger.log { "do not show ad due to rate limit" }
          false
        }
      }

      result
    }.also { deferredAd = it }
  }.await()
}

class AdLoadingException(val error: LoadAdError) : RuntimeException()

@Provide fun preloadFullScreenAdWorker(
  adsEnabled: Flow<AdsEnabled>,
  fullScreenAdManager: FullScreenAdManager
) = ScopeWorker<UiScope> {
  adsEnabled
    .filter { it.value }
    .collect { fullScreenAdManager.preloadAd() }
}
