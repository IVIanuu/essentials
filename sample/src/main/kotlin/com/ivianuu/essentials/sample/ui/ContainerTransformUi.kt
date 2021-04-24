package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Given
val containerTransformHomeItem = HomeItem("Container transform") { ContainerTransformKey }

object ContainerTransformKey : Key<Nothing>

@Given
fun containerTransformUi(@Given navigator: Navigator): KeyUi<ContainerTransformKey> = {
    ContainerTransformElement("opened", elevation = 8.dp) {
        Scaffold(topBar = { TopAppBar(title = { Text("Container transform") }) }) {
            LazyColumn {
                item {
                    ContainerTransformElement(
                        key = "big card",
                        modifier = Modifier.padding(8.dp),
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.height(300.dp)
                                .fillMaxWidth()
                                .clickable { navigator.push(ContainerTransformDetailsKey("big card")) },

                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(Color.Black.copy(alpha = 0.38f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    modifier = Modifier.size(100.dp),
                                    painter = painterResource(R.drawable.placeholder_image),
                                    contentDescription = null
                                )
                            }
                            ListItem(
                                title = { Text(Strings.Title) },
                                subtitle = { Text(Strings.Text) }
                            )
                            CompositionLocalProvider(
                                LocalContentAlpha provides ContentAlpha.medium
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    ),
                                    text = Strings.LoremIpsum,
                                    maxLines = 2,
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
