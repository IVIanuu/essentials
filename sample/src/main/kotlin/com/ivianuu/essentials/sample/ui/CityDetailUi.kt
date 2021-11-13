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

package com.ivianuu.essentials.sample.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.ui.animation.ContentAnimationElementKey
import com.ivianuu.essentials.ui.animation.animationElement
import com.ivianuu.essentials.ui.animation.transition.SharedElement
import com.ivianuu.essentials.ui.animation.transition.SharedElementStackTransition
import com.ivianuu.essentials.ui.animation.transition.animate
import com.ivianuu.essentials.ui.animation.transition.defaultAnimationSpec
import com.ivianuu.essentials.ui.animation.transition.fromElementModifier
import com.ivianuu.essentials.ui.animation.transition.toElementModifier
import com.ivianuu.essentials.ui.animation.util.fractionalTranslation
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiOptions
import com.ivianuu.essentials.ui.navigation.KeyUiOptionsFactory
import com.ivianuu.injekt.Provide

data class CityDetailKey(val city: City) : Key<Unit>

@Provide fun cityDetailUi(key: CityDetailKey): KeyUi<CityDetailKey> = {
  VerticalList(contentPadding = PaddingValues(0.dp)) {
    item {
      TopAppBar(
        title = { Text(key.city.name) },
        modifier = Modifier.animationElement("app bar")
      )
    }
    item {
      SharedElement(key = "image", isStart = false) {
        Image(
          modifier = Modifier.fillMaxWidth()
            .height(300.dp),
          painterResId = key.city.imageRes,
          contentScale = ContentScale.FillBounds
        )
      }
    }
    item {
      Card(
        Modifier.animationElement("card")
          .background(MaterialTheme.colors.surface)
      ) {
        Text(
          text = Strings.LoremIpsumChain,
          modifier = Modifier.padding(16.dp),
          style = MaterialTheme.typography.h6
        )
      }
    }
    item { Spacer(Modifier.height(LocalInsets.current.bottom)) }
  }
}

@Provide val cityDetailUiOptionsFactory: KeyUiOptionsFactory<CityDetailKey> = {
  val spec = defaultAnimationSpec(400.milliseconds, easing = FastOutSlowInEasing)
  KeyUiOptions(
    enterTransition = SharedElementStackTransition(
      "image ${it.city.name}" to "image",
      sharedElementAnimationSpec = spec,
      contentTransition = {
        val fromContentModifier = fromElementModifier(ContentAnimationElementKey)!!
        val card = toElementModifier("card")!!
        animate(spec) { value ->
          fromContentModifier.value = Modifier.fractionalTranslation(-value)
          card.value = Modifier.fractionalTranslation(yFraction = 1f - value)
        }
      }
    ),
    exitTransition = SharedElementStackTransition(
      "image ${it.city.name}" to "image",
      sharedElementAnimationSpec = spec,
      contentTransition = {
        val appBarModifier = fromElementModifier("app bar")!!
        val textModifier = fromElementModifier("card")!!
        animate(spec) { value ->
          appBarModifier.value = Modifier.alpha(1f - value)
          textModifier.value = Modifier.fractionalTranslation(
            yFraction = value,
            xFraction = value
          ).rotate(-180f * value)
        }
      }
    )
  )
}
