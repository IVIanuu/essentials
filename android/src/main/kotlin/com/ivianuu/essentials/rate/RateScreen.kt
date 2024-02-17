/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

object RateScreen : DialogScreen<Unit>

@Provide val rateUi = Ui<RateScreen, RateState> { state ->
  DialogScaffold(dismissible = false) {
    Dialog(
      content = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Image(
            painter = rememberAsyncImagePainter(AppIcon(state.packageName)),
            modifier = Modifier.size(96.dp),
            contentDescription = null
          )

          Text(
            text = stringResource(R.string.rate_title),
            style = MaterialTheme.typography.h6,
            color = LocalContentColor.current.copy(alpha = ContentAlpha.high)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            RATINGS.forEach { currentRating ->
              Icon(
                modifier = Modifier
                  .size(48.dp)
                  .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false)
                  ) { state.updateRating(currentRating) },
                painter = painterResource(R.drawable.ic_star),
                tint = if (currentRating <= state.rating) MaterialTheme.colors.secondary
                else LocalContentColor.current.copy(alpha = 0.12f),
                contentDescription = null
              )
            }
          }
        }
      },
      buttons = {
        if (state.displayShowNever) {
          TextButton(onClick = state.showNever) {
            Text(stringResource(R.string.never))
          }
        }

        TextButton(onClick = state.showLater) {
          Text(stringResource(R.string.later))
        }

        TextButton(enabled = state.confirmEnabled, onClick = state.confirm) {
          Text(stringResource(R.string.confirm))
        }
      }
    )
  }
}

data class RateState(
  val displayShowNever: Boolean,
  val packageName: String,
  val rating: Int,
  val confirm: () -> Unit,
  val showLater: () -> Unit,
  val showNever: () -> Unit,
  val updateRating: (Int) -> Unit,
) {
  val confirmEnabled: Boolean get() = rating != 0
}

@Provide fun ratePresenter(
  appConfig: AppConfig,
  navigator: Navigator,
  rateUseCases: RateUseCases
) = Presenter {
  var rating by remember { mutableStateOf(0) }
  RateState(
    displayShowNever = produceState(false) { value = rateUseCases.shouldDisplayShowNever() }.value,
    packageName = appConfig.packageName,
    rating = rating,
    showLater = action { rateUseCases.showLater() },
    showNever = action { rateUseCases.showNever() },
    updateRating = action { value -> rating = value },
    confirm = action {
      if (rating >= MIN_PLAY_RATING) navigator.replaceTop(RateOnPlayScreen)
      else navigator.replaceTop(FeedbackScreen)
    }
  )
}

private const val MIN_PLAY_RATING = 4
private val RATINGS = 1..5
