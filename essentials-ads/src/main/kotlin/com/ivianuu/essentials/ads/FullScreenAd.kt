/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.google.android.gms.ads.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlin.coroutines.*
import kotlin.time.*

interface FullScreenAd {
  suspend fun isLoaded(): Boolean

  fun preload()

  suspend fun load(): EsResult<Boolean, Throwable>

  suspend fun loadAndShow(): EsResult<Boolean, Throwable>

  suspend fun showIfLoaded(): Boolean
}

@JvmInline value class FullScreenAdId(val value: String)

data class FullScreenAdConfig(val adsInterval: Duration) {
  companion object {
    @Provide val defaultConfig: FullScreenAdConfig
      get() = FullScreenAdConfig(10.seconds)
  }
}

@Provide @Scoped<UiScope> class FullScreenAdImpl(
  private val id: FullScreenAdId,
  private val context: AppContext,
  private val clock: Clock,
  private val config: FullScreenAdConfig,
  private val mainContext: MainContext,
  private val scope: NamedCoroutineScope<AppScope>,
  private val showAds: Flow<ShowAds>,
  private val L: Logger
) : FullScreenAd {
  private val lock = Mutex()
  private var deferredAd: Deferred<suspend () -> Unit>? = null
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
    getOrCreateCurrentAd().invoke()
    preload()
    true
  }

  override suspend fun showIfLoaded() = getCurrentAd()
    ?.let {
      it.invoke()
      preload()
      true
    } ?: false

  private suspend fun getCurrentAd(): (suspend () -> Unit)? = lock.withLock {
    deferredAd?.takeUnless { it.isCompleted && it.getCompletionExceptionOrNull() != null }
      ?.await()
  }

  private suspend fun getOrCreateCurrentAd(): suspend () -> Unit = lock.withLock {
    deferredAd?.takeUnless {
      it.isCompleted && it.getCompletionExceptionOrNull() != null
    } ?: scope.async(mainContext) {
      val ad = InterstitialAd(context).apply {
        adUnitId = id.value
      }

      log { "start loading ad" }

      // wait until the has been loaded
      suspendCoroutine<Unit> { cont ->
        ad.adListener = object : AdListener() {
          override fun onAdLoaded() {
            super.onAdLoaded()
            cont.resume(Unit)
          }

          override fun onAdFailedToLoad(reason: Int) {
            super.onAdFailedToLoad(reason)
            cont.resumeWithException(AdLoadingException(reason))
          }
        }

        ad.loadAd(AdRequest.Builder().build())
      }

      log { "ad loaded" }

      val result: suspend () -> Unit = {
        if (rateLimiter.tryAcquire()) {
          log { "show ad" }
          lock.withLock { deferredAd = null }
          withContext(mainContext) {
            ad.show()
          }
        } else {
          log { "do not show ad due to rate limit" }
        }
      }

      result
    }.also { deferredAd = it }
  }.await()
}

class AdLoadingException(val reason: Int) : RuntimeException()

@Provide fun preloadFullScreenAdWorker(
  fullScreenAd: FullScreenAd,
  showAds: Flow<ShowAds>
) = ScopeWorker<UiScope> {
  showAds
    .filter { it.value }
    .collect { fullScreenAd.preload() }
}
