package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Given
val containerTransformHomeItem = HomeItem("Container transform") { ContainerTransformKey }

object ContainerTransformKey : Key<Nothing>

@Given
val containerTransformUi: KeyUi<ContainerTransformKey> = {
    ContainerTransformElement("opened", elevation = 8.dp) {
        Scaffold(topBar = { TopAppBar(title = { Text("Container transform") }) }) {
            LazyColumn {
                item {
                    Text(text = LOREM_IMPSUM_CHAIN, style = MaterialTheme.typography.h6)
                }
            }
        }
    }
}

@Given
val containerTransformUiOptions: KeyUiOptionsFactory<ContainerTransformKey> = {
    KeyUiOptions(transition = ContainerTransformStackTransition(
        "container Container transform",
        "opened"
    ))
}
