package com.ivianuu.essentials.ads

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.ComponentScope
import com.ivianuu.injekt.coroutines.MainDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@JvmInline value class FullScreenAdId(val value: String)

interface FullScreenAd {
  suspend fun isLoaded(): Boolean

  fun preload()

  suspend fun load(): Boolean

  suspend fun loadAndShow(): Boolean

  suspend fun showIfLoaded(): Boolean
}

@Provide @Scoped<UiComponent> class FullScreenAdImpl(
  private val id: FullScreenAdId,
  private val context: AppContext,
  private val mainDispatcher: MainDispatcher,
  private val scope: ComponentScope<AppComponent>,
  private val showAds: Flow<ShowAds>,
  private val L: Logger
) : FullScreenAd {
  private val lock = Mutex()
  private var deferredAd: Deferred<suspend () -> Unit>? = null

  override suspend fun isLoaded() = getCurrentAd() != null

  override fun preload() {
    launch { load() }
  }

  override suspend fun load(): Boolean {
    if (!showAds.first().value) return false
    getOrCreateCurrentAd()
    return true
  }

  override suspend fun loadAndShow(): Boolean {
    if (!showAds.first().value) return false
    getOrCreateCurrentAd().invoke()
    preload()
    return true
  }

  override suspend fun showIfLoaded(): Boolean = getCurrentAd()
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
    } ?: scope.async(mainDispatcher) {
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
        log { "show ad" }
        lock.withLock { deferredAd = null }
        withContext(mainDispatcher) {
          ad.show()
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
): ScopeWorker<UiComponent> = {
  showAds
    .filter { it.value }
    .collect { fullScreenAd.preload() }
}
