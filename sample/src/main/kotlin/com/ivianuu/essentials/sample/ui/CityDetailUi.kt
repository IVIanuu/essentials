/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.animation.util.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

data class CityDetailKey(val city: City) : Key<Unit>

@Provide fun cityDetailUi(key: CityDetailKey) = KeyUi<CityDetailKey> {
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

@Provide val cityDetailUiOptionsFactory = KeyUiOptionsFactory<CityDetailKey> {
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
