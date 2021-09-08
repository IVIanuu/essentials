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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.DisplayShowNeverUseCase
import com.ivianuu.essentials.rate.domain.ShowLaterUseCase
import com.ivianuu.essentials.rate.domain.ShowNeverUseCase
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object RateKey : DialogKey<Unit>

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
