/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.RateLimiter
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.util.ForegroundActivityProvider
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.MainContext
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.inject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
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
  suspend fun isFullScreenAdLoaded(): Boolean

  fun preloadFullScreenAd()

  suspend fun loadFullScreenAd(): Result<Boolean, Throwable>

  suspend fun loadAndShowFullScreenAd(): Result<Boolean, Throwable>

  suspend fun showFullScreenAdIfLoaded(): Boolean
}

@JvmInline value class FullScreenAdId(val value: String) {
  companion object {
    context(BuildInfo, ResourceProvider) @Provide fun default() = FullScreenAdId(
      loadResource(
        if (isDebug) R.string.es_test_ad_unit_id_interstitial
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

context(AdsEnabledProvider, AppContext, ForegroundActivityProvider, Logger, NamedCoroutineScope<AppScope>)
@Provide @Scoped<UiScope> class FullScreenAdManagerImpl(
  private val id: FullScreenAdId,
  private val config: FullScreenAdConfig,
  private val mainContext: MainContext
) : FullScreenAdManager {
  private val lock = Mutex()
  private var deferredAd: Deferred<suspend () -> Boolean>? = null
  private val rateLimiter = RateLimiter(1, config.adsInterval)

  override suspend fun isFullScreenAdLoaded() = getCurrentAd() != null

  override fun preloadFullScreenAd() {
    launch { loadFullScreenAd() }
  }

  override suspend fun loadFullScreenAd() = catch {
    if (!adsEnabled.first()) return@catch false
    getOrCreateCurrentAd()
    true
  }

  override suspend fun loadAndShowFullScreenAd() = catch {
    if (!adsEnabled.first()) return@catch false
    getOrCreateCurrentAd()
      .also { preloadFullScreenAd() }
      .invoke()
  }

  override suspend fun showFullScreenAdIfLoaded() = getCurrentAd()
    ?.let {
      it.invoke()
        .also { preloadFullScreenAd() }
    } ?: false

  private suspend fun getCurrentAd(): (suspend () -> Boolean)? = lock.withLock {
    deferredAd?.takeUnless { it.isCompleted && it.getCompletionExceptionOrNull() != null }
      ?.await()
  }

  private suspend fun getOrCreateCurrentAd(): suspend () -> Boolean = lock.withLock {
    deferredAd?.takeUnless {
      it.isCompleted && it.getCompletionExceptionOrNull() != null
    } ?: async(mainContext) {
      log { "start loading ad" }

      val ad = suspendCoroutine<InterstitialAd> { cont ->
        InterstitialAd.load(
          inject(),
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

      log { "ad loaded" }

      val result: suspend () -> Boolean = {
        if (rateLimiter.tryAcquire()) {
          log { "show ad" }
          lock.withLock { deferredAd = null }
          withContext(mainContext) {
            ad.show(foregroundActivity.first()!!)
          }
          true
        } else {
          log { "do not show ad due to rate limit" }
          false
        }
      }

      result
    }.also { deferredAd = it }
  }.await()
}

class AdLoadingException(val error: LoadAdError) : RuntimeException()

context(AdsEnabledProvider)
    @Provide fun preloadFullScreenAdWorker(fullScreenAdManager: FullScreenAdManager) =
  ScopeWorker<UiScope> {
    adsEnabled
      .filter { it }
      .collect { fullScreenAdManager.preloadFullScreenAd() }
  }
