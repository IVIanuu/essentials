/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.activity.*
import androidx.compose.runtime.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.*
import essentials.*
import essentials.app.*
import essentials.compose.*
import essentials.coroutines.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

@Stable @Provide @Scoped<UiScope> class FullScreenAds(
  private val activity: ComponentActivity,
  private val appContext: AppContext,
  private val adsEnabledProducer: @Composable () -> AdsEnabled,
  config: @FinalAdConfig FullScreenAdConfig,
  private val coroutineContexts: CoroutineContexts,
  @property:Provide private val scope: Scope<UiScope> = inject
) {
  private var currentAd by mutableStateOf<FullScreenAd?>(null)
  private val rateLimiter = RateLimiter(1, config.adsInterval)
  private var adsEnabled by mutableStateOf(false)

  init {
    launchMolecule {
      adsEnabled = adsEnabledProducer()

      if (!adsEnabled) {
        d { "ads not enabled" }
        return@launchMolecule
      }

      if (currentAd == null)
        LaunchedEffect(true) {
          var attempt = 1
          while (currentCoroutineContext().isActive) {
            d { "load ad $attempt" }

            val ad = catch {
              suspendCoroutine { cont ->
                InterstitialAd.load(
                  appContext,
                  config.id,
                  AdRequest.Builder().build(),
                  object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                      d { "ad loaded" }
                      cont.resume(FullScreenAd(ad))
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                      e { "ad failed to load $error" }
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
        d { "clear ad and preload next" }
        currentAd = null
      }
    }
  }

  suspend fun showAd(timeout: Duration = 2.seconds): Boolean {
    if (!adsEnabled) return false
    return withTimeoutOrNull(timeout) {
      snapshotFlow { currentAd }.first { it != null }!!.show()
    } ?: false
  }

  @Stable private inner class FullScreenAd(private val interstitial: InterstitialAd) {
    var wasShown by mutableStateOf(false)

    suspend fun show(): Boolean {
      if (scope.scopeOfOrNull<AppVisibleScope>() == null) return false
        .also { d { "do not show -> not in foreground" } }

      if (!rateLimiter.tryAcquire()) return false
        .also { d { "do not show -> rate limit reached" } }

      wasShown = true

      d { "show ad" }

      return withContext(coroutineContexts.main) {
        suspendCoroutine { cont ->
          interstitial.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
              d { "on ad dismissed" }
              cont.resume(true)
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
              d { "on failed to show ad $p0" }
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
