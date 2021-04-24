package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
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
        Scaffold(
            topBar = { TopAppBar(title = { Text("Container transform") }) },
            floatingActionButton = {
                ContainerTransformElement(
                    key = "fab",
                    color = MaterialTheme.colors.secondary,
                    cornerSize = 28.dp,
                    elevation = 6.dp
                ) {
                    FloatingActionButton(
                        onClick = { navigator.push(ContainerTransformDetailsKey("fab")) },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                    }
                }
            }
        ) {
            LazyColumn {
                item { BigDetailsCard(navigator) }
                item { SmallDetailsCard(navigator) }
                items(10) { DetailsListItem(it, navigator) }
            }
        }
    }
}

@Composable
private fun BigDetailsCard(navigator: Navigator) {
    ContainerTransformElement(
        key = "big card",
        modifier = Modifier.padding(8.dp),
        cornerSize = 4.dp,
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

@Composable
private fun SmallDetailsCard(navigator: Navigator) {
    ContainerTransformElement(
        key = "small card",
        modifier = Modifier.padding(8.dp),
        cornerSize = 4.dp,
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.height(125.dp)
                .fillMaxWidth()
                .clickable { navigator.push(ContainerTransformDetailsKey("small card")) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(125.dp)
                    .background(Color.Black.copy(alpha = 0.38f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.padding(16.dp),
                    painter = painterResource(R.drawable.placeholder_image),
                    contentDescription = null
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = Strings.Title,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = Strings.LoremIpsum,
                        maxLines = 2,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsListItem(
    index: Int,
    navigator: Navigator
) {
    ContainerTransformElement("list item $index") {
        ListItem(
            leading = {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(R.drawable.avatar_logo),
                    contentDescription = null
                )
            },
            title = { Text("List item $index") },
            subtitle = { Text(Strings.Text) },
            onClick = { navigator.push(ContainerTransformDetailsKey("list item $index")) }
        )
    }
}
