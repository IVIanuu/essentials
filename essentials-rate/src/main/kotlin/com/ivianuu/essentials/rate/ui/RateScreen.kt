/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.RateUseCases
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.dialog.DialogScreen
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.replaceTop
import com.ivianuu.injekt.Provide

object RateScreen : DialogScreen<Unit>

@Provide val rateUi = Ui<RateScreen, RateModel> { model ->
  DialogScaffold(dismissible = false) {
    Dialog(
      content = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Image(
            painter = rememberAsyncImagePainter(AppIcon(model.packageName)),
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
            RATINGS.forEach { currentRating ->
              Icon(
                modifier = Modifier
                  .size(48.dp)
                  .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false)
                  ) { model.updateRating(currentRating) },
                painterResId = R.drawable.es_ic_star,
                tint = if (currentRating <= model.rating) MaterialTheme.colors.secondary
                else LocalContentColor.current.copy(alpha = 0.12f)
              )
            }
          }
        }
      },
      buttons = {
        if (model.displayShowNever) {
          TextButton(onClick = model.showNever) {
            Text(R.string.es_never)
          }
        }

        TextButton(onClick = model.showLater) {
          Text(R.string.es_later)
        }

        TextButton(enabled = model.confirmEnabled, onClick = model.confirm) {
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
  appConfig: AppConfig,
  navigator: Navigator,
  rateUseCases: RateUseCases
) = Model {
  var rating by remember { mutableStateOf(0) }
  RateModel(
    displayShowNever = produceState(false) { value = rateUseCases.shouldDisplayShowNever() }.value,
    packageName = appConfig.packageName,
    rating = rating,
    showLater = action { rateUseCases.showLater() },
    showNever = action { rateUseCases.showNever() },
    updateRating = action { value -> rating = value },
    confirm = action {
      if (rating >= MIN_PLAY_RATING) {
        navigator.replaceTop(RateOnPlayScreen)
      } else {
        navigator.replaceTop(FeedbackScreen)
      }
    }
  )
}

private const val MIN_PLAY_RATING = 4
private val RATINGS = 1..5
