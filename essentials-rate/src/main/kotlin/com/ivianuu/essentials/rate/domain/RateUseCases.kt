package com.ivianuu.essentials.rate.domain

import androidx.activity.*
import com.github.michaelbull.result.*
import com.google.android.play.core.review.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.rate.data.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

typealias RateOnPlayUseCase = suspend () -> Unit

@Given
fun rateOnPlayUseCase(
  @Given activity: ComponentActivity,
  @Given pref: DataStore<RatePrefs>
): RateOnPlayUseCase = {
  catch {
    val reviewManagerFactory = ReviewManagerFactory.create(activity)
    val reviewInfo = suspendCoroutine<ReviewInfo> { cont ->
      reviewManagerFactory.requestReviewFlow()
        .addOnSuccessListener { cont.resume(it) }
        .addOnFailureListener { cont.resumeWithException(it) }
    }
    suspendCoroutine<Unit> { cont ->
      reviewManagerFactory.launchReviewFlow(activity, reviewInfo)
        .addOnSuccessListener { cont.resume(Unit) }
        .addOnFailureListener { cont.resumeWithException(it) }
    }
    pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.COMPLETED) }
  }.onFailure { it.printStackTrace() }
}

internal typealias DisplayShowNeverUseCase = suspend () -> Boolean

@Given fun displayShowNeverUseCase(
  @Given pref: DataStore<RatePrefs>
): DisplayShowNeverUseCase = {
  pref.data.first().feedbackState == RatePrefs.FeedbackState.LATER
}

internal typealias ShowNeverUseCase = suspend () -> Unit

@Given fun showNeverUseCase(
  @Given key: Key<*>,
  @Given navigator: Navigator,
  @Given pref: DataStore<RatePrefs>
): ShowNeverUseCase = {
  pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.NEVER) }
  navigator.pop(key)
}

internal typealias ShowLaterUseCase = suspend () -> Unit

@Given fun showLaterUseCase(
  @Given key: Key<*>,
  @Given navigator: Navigator,
  @Given pref: DataStore<RatePrefs>,
  @Given timestampProvider: TimestampProvider
): ShowLaterUseCase = {
  pref.updateData {
    copy(
      launchTimes = 0,
      installTime = timestampProvider().toLongMilliseconds(),
      feedbackState = RatePrefs.FeedbackState.LATER
    )
  }
  navigator.pop(key)
}
