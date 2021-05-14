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
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.compose.*
import kotlinx.coroutines.*

@Given val containerTransformHomeItem = HomeItem("Container transform") { ContainerTransformKey }

object ContainerTransformKey : Key<Nothing>

@Given fun containerTransformUi(@Given navigator: Navigator): KeyUi<ContainerTransformKey> = {
  var listInfo by rememberScopedValue(key = "list_state") {
    mutableStateOf(0 to 0)
  }
  ContainerTransformSurface(key = "opened", elevation = 8.dp, isOpened = true) {
    Scaffold(
      topBar = { TopAppBar(title = { Text("Container transform") }) },
      floatingActionButton = {
        ContainerTransformSurface(
          key = "fab",
          isOpened = false,
          color = MaterialTheme.colors.secondary,
          cornerSize = 28.dp,
          elevation = 6.dp
        ) {
          val scope = rememberCoroutineScope()
          Box(
            modifier = Modifier
              .size(56.dp)
              .clickable {
                scope.launch {
                  navigator.push(ContainerTransformDetailsKey("fab"))
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null
            )
          }
        }
      }
    ) {
      val listState = rememberLazyListState(listInfo.first, listInfo.second)
      listInfo = listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
      LazyColumn(state = listState, contentPadding = localVerticalInsetsPadding()) {
        item { BigDetailsCard(navigator) }
        item { SmallDetailsCard(navigator) }
        item {
          Row {
            DetailsGridCard(Modifier.weight(0.5f), "grid item 0_0", navigator)
            DetailsGridCard(Modifier.weight(0.5f), "grid item 0_1", navigator)
          }
        }
        item {
          Row {
            DetailsGridCard(Modifier.weight(0.33f), "grid item 1_0", navigator)
            DetailsGridCard(Modifier.weight(0.33f), "grid item 1_1", navigator)
            DetailsGridCard(Modifier.weight(0.33f), "grid item 1_2", navigator)
          }
        }
        items(10) { DetailsListItem(it, navigator) }
      }
    }
  }
}

@Composable private fun BigDetailsCard(navigator: Navigator) {
  ContainerTransformSurface(
    key = "big card",
    isOpened = false,
    modifier = Modifier.padding(8.dp),
    cornerSize = 4.dp,
    elevation = 2.dp
  ) {
    val scope = rememberCoroutineScope()
    Column(
      modifier = Modifier.height(300.dp)
        .fillMaxWidth()
        .clickable {
          scope.launch {
            navigator.push(ContainerTransformDetailsKey("big card"))
          }
        },
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

@Composable private fun SmallDetailsCard(navigator: Navigator) {
  ContainerTransformSurface(
    key = "small card",
    isOpened = false,
    modifier = Modifier.padding(8.dp),
    cornerSize = 4.dp,
    elevation = 2.dp
  ) {
    val scope = rememberCoroutineScope()
    Row(
      modifier = Modifier.height(125.dp)
        .fillMaxWidth()
        .clickable {
          scope.launch {
            navigator.push(ContainerTransformDetailsKey("small card"))
          }
        },
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

@Composable private fun DetailsGridCard(
  modifier: Modifier = Modifier,
  key: Any,
  navigator: Navigator
) {
  ContainerTransformSurface(
    key = key,
    isOpened = false,
    modifier = modifier
      .padding(8.dp),
    cornerSize = 4.dp,
    elevation = 2.dp
  ) {
    val scope = rememberCoroutineScope()
    Column(
      modifier = Modifier.clickable {
        scope.launch {
          navigator.push(ContainerTransformDetailsKey(key))
        }
      }
    ) {
      Box(
        modifier = Modifier
          .height(100.dp)
          .fillMaxWidth()
          .background(Color.Black.copy(alpha = 0.38f)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          modifier = Modifier.padding(16.dp),
          painter = painterResource(R.drawable.placeholder_image),
          contentDescription = null
        )
      }

      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center
      ) {
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

@Composable private fun DetailsListItem(
  index: Int,
  navigator: Navigator
) {
  ContainerTransformSurface(
    key = "list item $index",
    isOpened = false
  ) {
    val scope = rememberCoroutineScope()
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
      onClick = {
        scope.launch {
          navigator.push(ContainerTransformDetailsKey("list item $index"))
        }
      }
    )
  }
}
