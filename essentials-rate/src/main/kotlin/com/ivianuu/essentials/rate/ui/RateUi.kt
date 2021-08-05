/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.rate.ui

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
import com.ivianuu.essentials.apps.coil.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

object RateKey : DialogKey<Nothing>

@Provide val rateUi: ModelKeyUi<RateKey, RateModel> = {
  DialogScaffold(dismissible = false) {
    Dialog(
      content = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Image(
            painter = rememberImagePainter(AppIcon(model.packageName)),
            modifier = Modifier.size(96.dp),
            contentDescription = null
          )

          Spacer(Modifier.height(16.dp))

          Text(
            text = stringResource(R.string.es_rate_title),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface
              .copy(alpha = ContentAlpha.high)
          )

          Spacer(Modifier.height(16.dp))

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
                  ) { model.updateRating(rating) },
                painter = painterResource(R.drawable.es_ic_star),
                contentDescription = null,
                tint = if (rating <= model.rating) MaterialTheme.colors.secondary
                else MaterialTheme.colors.onSurface
                  .copy(alpha = 0.12f)
              )
            }
          }
        }
      },
      buttons = {
        if (model.displayShowNever) {
          TextButton(onClick = model.showNever) {
            Text(stringResource(R.string.es_never))
          }
        }

        TextButton(onClick = model.showLater) {
          Text(stringResource(R.string.es_later))
        }

        TextButton(enabled = model.confirmEnabled, onClick = model.confirm) {
          Text(stringResource(R.string.es_confirm))
        }
      }
    )
  }
}

@Optics data class RateModel(
  val displayShowNever: Boolean = false,
  val packageName: String = "",
  val rating: Int = 0,
  val confirm: () -> Unit = {},
  val showLater: () -> Unit = {},
  val showNever: () -> Unit = {},
  val updateRating: (Int) -> Unit = {},
) {
  val confirmEnabled: Boolean get() = rating != 0

  companion object {
    @Provide fun initial(buildInfo: BuildInfo): @Initial RateModel = RateModel(
      packageName = buildInfo.packageName
    )
  }
}

@Provide fun rateModel(
  initial: @Initial RateModel,
  displayShowNever: DisplayShowNeverUseCase,
  navigator: Navigator,
  scope: InjektCoroutineScope<KeyUiScope>,
  showLater: ShowLaterUseCase,
  showNever: ShowNeverUseCase
): @Scoped<KeyUiScope> StateFlow<RateModel> = scope.state(initial) {
  launch {
    val showDoNotShowAgain = displayShowNever()
    update { copy(displayShowNever = showDoNotShowAgain) }
  }
  action(RateModel.showLater()) { showLater() }
  action(RateModel.showNever()) { showNever() }
  action(RateModel.updateRating()) { value -> update { copy(rating = value) } }
  action(RateModel.confirm()) {
    val rating = state.first().rating
    if (rating >= MIN_PLAY_RATING) {
      navigator.replaceTop(RateOnPlayKey)
    } else {
      navigator.replaceTop(FeedbackKey)
    }
  }
}

private const val MIN_PLAY_RATING = 4
private val RATINGS = 1..5
