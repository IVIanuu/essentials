/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

object RateKey : PopupKey<Unit>

@Provide val rateUi = ModelKeyUi<RateKey, RateModel> {
  DialogScaffold(dismissible = false) {
    Dialog(
      content = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Image(
            painter = rememberImagePainter(AppIcon(packageName)),
            modifier = Modifier.size(96.dp)
          )

          Text(
            textResId = R.string.es_rate_title,
            style = MaterialTheme.typography.h6,
            color = LocalContentColor.current.copy(alpha = ContentAlpha.high)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            RATINGS.forEach { rating ->
              Icon(
                modifier = Modifier
                  .size(48.dp)
                  .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false)
                  ) { updateRating(rating) },
                painterResId = R.drawable.es_ic_star,
                tint = if (rating <= rating) MaterialTheme.colors.secondary
                else LocalContentColor.current.copy(alpha = 0.12f)
              )
            }
          }
        }
      },
      buttons = {
        if (displayShowNever) {
          TextButton(onClick = showNever) {
            Text(R.string.es_never)
          }
        }

        TextButton(onClick = showLater) {
          Text(R.string.es_later)
        }

        TextButton(enabled = confirmEnabled, onClick = confirm) {
          Text(R.string.es_confirm)
        }
      }
    )
  }
}

data class RateModel(
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

@Provide fun rateModel(
  buildInfo: BuildInfo,
  displayShowNever: DisplayShowNeverUseCase,
  showLater: ShowLaterUseCase,
  showNever: ShowNeverUseCase,
  ctx: KeyUiContext<RateKey>
) = Model {
  var rating by remember { mutableStateOf(0) }
  RateModel(
    displayShowNever = produce(false) { displayShowNever() },
    packageName = buildInfo.packageName,
    rating = rating,
    showLater = action { showLater() },
    showNever = action { showNever() },
    updateRating = action { value -> rating = value },
    confirm = action {
      if (rating >= MIN_PLAY_RATING) {
        ctx.navigator.replaceTop(RateOnPlayKey)
      } else {
        ctx.navigator.replaceTop(FeedbackKey)
      }
    }
  )
}

private const val MIN_PLAY_RATING = 4
private val RATINGS = 1..5
