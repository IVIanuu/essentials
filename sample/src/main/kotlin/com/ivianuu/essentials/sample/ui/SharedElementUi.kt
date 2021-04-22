package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.Given

@Given
val sharedElementsHomeItem = HomeItem("Shared element") { SharedElementKey(it) }

data class SharedElementKey(val color: Color) : Key<Nothing>

@Given
fun sharedElementKeyUi(@Given key: SharedElementKey): KeyUi<SharedElementKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Shared Elements") }) }
    ) {
        Box {
            SharedElement(key = "shared_element", modifier = Modifier.center()) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(key.color, CircleShape)
                )
            }
        }
    }
}

@Given
fun sharedElementsNavigationOptionFactory(): KeyUiOptionsFactory<SharedElementKey> = {
    KeyUiOptions(
        enterTransition = SharedElementStackTransition("Shared element" to "shared_element"),
        exitTransition = SharedElementStackTransition("Shared element" to "shared_element")
    )
}
