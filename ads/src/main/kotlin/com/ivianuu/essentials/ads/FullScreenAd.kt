/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.activity.ComponentActivity
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
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.RateLimiter
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.result.Result
import com.ivianuu.essentials.result.catch
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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface FullScreenAdManager {
  suspend fun isAdLoaded(): Boolean

  fun preloadAd()

  suspend fun loadAd(): Result<Boolean, Throwable>

  suspend fun loadAndShowAd(): Result<Boolean, Throwable>

  suspend fun showAdIfLoaded(): Boolean
}

data class FullScreenAdConfig(val id: String, val adsInterval: Duration = 30.seconds) {
  @Provide companion object {
    @Provide fun final(
      adConfig: FullScreenAdConfig,
      appConfig: AppConfig,
      resources: Resources,
    ): @FinalAdConfig FullScreenAdConfig = if (!appConfig.isDebug) adConfig
    else adConfig.copy(id = resources(R.string.es_test_ad_unit_id_full_screen))
  }
}

@Provide @Scoped<UiScope> class FullScreenAdManagerImpl(
  private val activity: ComponentActivity,
  private val appContext: AppContext,
  private val adsEnabledFlow: Flow<AdsEnabled>,
  private val config: @FinalAdConfig FullScreenAdConfig,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<UiScope>,
  private val scopeManager: ScopeManager
) : FullScreenAdManager {
  private val lock = Mutex()
  private var deferredAd: Deferred<suspend () -> Boolean>? = null
  private val rateLimiter = RateLimiter(1, config.adsInterval)

  init {
    scope.launch { preloadAd() }
  }

  override suspend fun isAdLoaded() = getCurrentAd() != null

  override fun preloadAd() {
    scope.launch { loadAd() }
  }

  override suspend fun loadAd() = catch {
    if (!adsEnabledFlow.first().value) return@catch false
    getOrCreateCurrentAd()
    true
  }

  override suspend fun loadAndShowAd() = catch {
    if (!adsEnabledFlow.first().value) return@catch false
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
          if (scopeManager.scopeOfOrNull<AppVisibleScope>().first() != null)
            withContext(coroutineContexts.main) {
              ad.show(activity)
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