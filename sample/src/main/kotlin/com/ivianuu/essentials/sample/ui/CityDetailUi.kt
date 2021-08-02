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

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.animation.util.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlin.time.*

data class CityDetailKey(val city: City) : Key<Nothing>

@Provide fun cityDetailUi(key: CityDetailKey): KeyUi<CityDetailKey> = {
  LazyColumn {
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
          painter = painterResource(key.city.imageRes),
          contentDescription = null,
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
