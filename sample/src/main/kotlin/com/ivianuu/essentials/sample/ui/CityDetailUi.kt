package com.ivianuu.essentials.sample.ui

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

@Given
fun cityDetailUi(@Given key: CityDetailKey): KeyUi<CityDetailKey> = {
    LazyColumn {
        item {
            TopAppBar(
                title = { Text(key.city.name) },
                modifier = Modifier.animationElement("app bar")
            )
        }
        item {
            SharedElement("image") {
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
                    "• This is a city.\n" +
                            "• There's some cool stuff about it.\n" +
                            "• But really this is just a demo, not a city guide app.\n" +
                            "• This demo is meant to show some nice transitions.\n" +
                            "• You should have seen some sweet shared element transitions using the ImageView and the TextView in the \"header\" above.\n" +
                            "• This transition utilized some callbacks to ensure all the necessary rows in the RecyclerView were laid about before the transition occurred.\n" +
                            "• Just adding some more lines so it scrolls now...\n\n\n\n\n\n\nThe end.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.h6
                )
            }
        }
        item { Spacer(Modifier.height(LocalInsets.current.bottom)) }
    }
}

@Given
val cityDetailUiOptionsFactory: KeyUiOptionsFactory<CityDetailKey> = {
    val spec = defaultAnimationSpec(500.milliseconds)
    KeyUiOptions(
        enterTransition = SharedElementStackTransition(
            "image ${it.city.name}" to "image",
            sharedElementAnimationSpec = spec,
            contentTransition = {
                val fromContentModifier = fromElementModifier(ContentAnimationElementKey)!!
                val card = toElementModifier("card")!!
                animate(spec) {
                    fromContentModifier.value = Modifier.fractionalTranslation(-value)
                    card.value = Modifier.fractionalTranslation(translationYFraction = 1f - value)
                }
            }
        ),
        exitTransition = SharedElementStackTransition(
            "image ${it.city.name}" to "image",
            sharedElementAnimationSpec = spec,
            contentTransition = {
                val appBarModifier = fromElementModifier("app bar")!!
                val textModifier = fromElementModifier("card")!!
                animate(spec) {
                    appBarModifier.value = Modifier.alpha(1f - value)
                    textModifier.value = Modifier.fractionalTranslation(
                        translationYFraction = value,
                        translationXFraction = value
                    ).rotate(-180f * value)
                }
            }
        )
    )
}
