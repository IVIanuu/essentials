/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.activity.*
import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlin.coroutines.*
import kotlin.math.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

@Stable @Provide @Scoped<UiScope> class FullScreenAdManager(
  private val activity: ComponentActivity,
  private val appContext: AppContext,
  private val appScope: Scope<AppScope>,
  private val adsEnabledState: State<AdsEnabled>,
  config: @FinalAdConfig FullScreenAdConfig,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  scope: ScopedCoroutineScope<UiScope>
) {
  private var currentAd by mutableStateOf<FullScreenAd?>(null)
  private val rateLimiter = RateLimiter(1, config.adsInterval)

  init {
    scope.launchMolecule {
      if (!adsEnabledState.value.value) {
        logger.d { "ads not enabled" }
        return@launchMolecule
      }

      if (currentAd == null)
        LaunchedEffect(true) {
          var attempt = 1
          while (currentCoroutineContext().isActive) {
            logger.d { "load ad $attempt" }

            val ad = catch {
              suspendCoroutine { cont ->
                InterstitialAd.load(
                  appContext,
                  config.id,
                  AdRequest.Builder().build(),
                  object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                      logger.d { "ad loaded" }
                      cont.resume(FullScreenAd(ad))
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                      logger.e { "ad failed to load $error" }
                      cont.resumeWithException(IllegalStateException("$error"))
                    }
                  }
                )
              }
            }
              .printErrors()
              .getOrNull()

            if (ad == null) {
              attempt++
              delay(1.seconds * attempt)
              continue
            }

            currentAd = ad
            break
          }
        }

      if (currentAd?.wasShown == true) {
        logger.d { "clear ad and preload next" }
        currentAd = null
      }
    }
  }

  suspend fun showAd(timeout: Duration = 2.seconds): Boolean {
    if (!adsEnabledState.value.value) return false
    return withTimeoutOrNull(timeout) {
      snapshotFlow { currentAd }.first { it != null }!!.show()
    } ?: false
  }

  @Stable private inner class FullScreenAd(private val interstitial: InterstitialAd) {
    var wasShown by mutableStateOf(false)

    suspend fun show(): Boolean {
      if (appScope.scopeOfOrNull<AppVisibleScope>() == null) return false
        .also { logger.d { "do not show -> not in foreground" } }

      if (!rateLimiter.tryAcquire()) return false
        .also { logger.d { "do not show -> rate limit reached" } }

      wasShown = true

      logger.d { "show ad" }

      return withContext(coroutineContexts.main) {
        suspendCoroutine { cont ->
          interstitial.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
              logger.d { "on ad dismissed" }
              cont.resume(true)
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
              logger.d { "on failed to show ad $p0" }
              cont.resume(false)
            }
          }
          interstitial.show(activity)
        }
      }
    }
  }
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
