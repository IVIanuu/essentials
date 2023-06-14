package com.ivianuu.essentials.sample.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.ivianuu.essentials.ui.layout.navigationBarsPadding
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenConfig
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide

data class CityDetailScreen(val city: City) : Screen<Unit> {
  companion object {
    @Provide fun config(screen: CityDetailScreen): ScreenConfig<CityDetailScreen> {
      val spec = defaultAnimationSpec(400.milliseconds, easing = FastOutSlowInEasing)
      return ScreenConfig(
        enterTransition = SharedElementStackTransition(
          "image ${screen.city.name}" to "image",
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
          "image ${screen.city.name}" to "image",
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
  }
}

@Provide fun cityDetailUi(key: CityDetailScreen) = Ui<CityDetailScreen, Unit> {
  VerticalList(
    topPaddingModifier = Modifier,
    bottomPaddingModifier = Modifier
  ) {
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
    item { Spacer(Modifier.navigationBarsPadding()) }
  }
}
