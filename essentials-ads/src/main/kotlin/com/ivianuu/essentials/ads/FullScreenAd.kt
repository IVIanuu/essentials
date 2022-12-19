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
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.time.Clock
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.util.ForegroundActivity
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.MainContext
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
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

interface FullScreenAd {
  suspend fun isLoaded(): Boolean

  fun preload()

  suspend fun load(): Result<Boolean, Throwable>

  suspend fun loadAndShow(): Result<Boolean, Throwable>

  suspend fun showIfLoaded(): Boolean
}

@JvmInline value class FullScreenAdId(val value: String) {
  companion object {
    @Provide fun default(
      buildInfo: BuildInfo,
      RP: ResourceProvider
    ) = FullScreenAdId(
      loadResource(
        if (buildInfo.isDebug) R.string.es_test_ad_unit_id_interstitial
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

@Provide @Scoped<UiScope> class FullScreenAdImpl(
  private val id: FullScreenAdId,
  private val context: AppContext,
  private val clock: Clock,
  private val config: FullScreenAdConfig,
  private val foregroundActivity: Flow<ForegroundActivity>,
  private val mainContext: MainContext,
  private val scope: NamedCoroutineScope<AppScope>,
  private val showAds: Flow<ShowAds>,
  private val L: Logger
) : FullScreenAd {
  private val lock = Mutex()
  private var deferredAd: Deferred<suspend () -> Boolean>? = null
  private val rateLimiter = RateLimiter(1, config.adsInterval)

  override suspend fun isLoaded() = getCurrentAd() != null

  override fun preload() {
    scope.launch { load() }
  }

  override suspend fun load() = catch {
    if (!showAds.first().value) return@catch false
    getOrCreateCurrentAd()
    true
  }

  override suspend fun loadAndShow() = catch {
    if (!showAds.first().value) return@catch false
    getOrCreateCurrentAd()
      .also { preload() }
      .invoke()
  }

  override suspend fun showIfLoaded() = getCurrentAd()
    ?.let {
      it.invoke()
        .also { preload() }
    } ?: false

  private suspend fun getCurrentAd(): (suspend () -> Boolean)? = lock.withLock {
    deferredAd?.takeUnless { it.isCompleted && it.getCompletionExceptionOrNull() != null }
      ?.await()
  }

  private suspend fun getOrCreateCurrentAd(): suspend () -> Boolean = lock.withLock {
    deferredAd?.takeUnless {
      it.isCompleted && it.getCompletionExceptionOrNull() != null
    } ?: scope.async(mainContext) {
      log { "start loading ad" }

      val ad = suspendCoroutine<InterstitialAd> { cont ->
        InterstitialAd.load(
          context,
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

@Provide fun preloadFullScreenAdWorker(
  fullScreenAd: FullScreenAd,
  showAds: Flow<ShowAds>
) = ScopeWorker<UiScope> {
  showAds
    .filter { it.value }
    .collect { fullScreenAd.preload() }
}
