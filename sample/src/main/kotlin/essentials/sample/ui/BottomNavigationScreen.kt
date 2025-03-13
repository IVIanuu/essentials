/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalSharedTransitionApi::class)

package essentials.sample.ui

import android.annotation.*
import androidx.activity.compose.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

@Provide val bottomNavigationHomeItem = HomeItem("Bottom navigation") { BottomNavigationScreen() }

class BottomNavigationScreen : Screen<Unit>

@Provide @Composable fun BottomNavigationUi(): Ui<BottomNavigationScreen> {
  var selectedItem by remember { mutableStateOf(BottomNavItem.entries.first()) }

  EsScaffold(
    topBar = { EsAppBar { Text("Bottom navigation") } },
    bottomBar = {
      EsNavigationBar {
        BottomNavItem.entries.fastForEach { item ->
          NavigationBarItem(
            alwaysShowLabel = true,
            selected = item == selectedItem,
            onClick = { selectedItem = item },
            icon = { Icon(item.icon, null) },
            label = { Text(item.title) }
          )
        }
      }
    }
  ) {
    CompositionLocalProvider(
      LocalContentPadding provides LocalContentPadding.current.let {
        PaddingValues(
          start = it.calculateStartPadding(LocalLayoutDirection.current),
          top = it.calculateTopPadding(),
          end = it.calculateEndPadding(LocalLayoutDirection.current),
          bottom = it.calculateBottomPadding() + CollapsedPlayerHeight + CollapsedPlayerPadding
        )
      }
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        @SuppressLint("UnusedContentLambdaTargetStateParameter")
        AnimatedContent(selectedItem) { item ->
          EsLazyColumn {
            (1..100).forEach { item ->
              item {
                EsListItem(headlineContent = { Text("Item $item") })
              }
            }
          }
        }
      }

    }
  }

  PlaybackUiTest()
}

private enum class BottomNavItem(
  val title: String,
  val icon: ImageVector
) {
  HOME(
    title = "Home",
    icon = Icons.Default.Home
  ),
  MAILS(
    title = "Mails",
    icon = Icons.Default.Email
  ),
  SEARCH(
    title = "Search",
    icon = Icons.Default.Search
  ),
  SCHEDULE(
    title = "Schedule",
    icon = Icons.Default.ViewAgenda
  ),
  SETTINGS(
    title = "Settings",
    icon = Icons.Default.Settings
  )
}

enum class SlideUpPanelState {
  EXPANDED, COLLAPSED
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable fun PlaybackUiTest() {
  var isExpanded by remember { mutableStateOf(false) }
  BackHandler(isExpanded) { isExpanded = false }

  val containerColor = MaterialTheme.colorScheme.primary

  SharedTransitionLayout {
    AnimatedContent(
      targetState = isExpanded
    ) { currentExpanded ->
      if (currentExpanded) {
        ExpandedPlayer(
          containerColor = containerColor,
          animatedVisibilityScope = this,
          sharedTransitionScope = this@SharedTransitionLayout
        ) { isExpanded = false }
      } else {
        CollapsedPlayer(
          containerColor = containerColor,
          animatedVisibilityScope = this,
          sharedTransitionScope = this@SharedTransitionLayout
        ) { isExpanded = true }
      }
    }
  }
}

val CollapsedPlayerHeight = 56.dp
val CollapsedPlayerPadding = 12.dp

@Composable fun CollapsedPlayer(
  containerColor: Color,
  animatedVisibilityScope: AnimatedVisibilityScope,
  sharedTransitionScope: SharedTransitionScope,
  onExpandClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.BottomCenter)
      .systemBarsPadding()
      .padding(bottom = 80.dp)
      .padding(CollapsedPlayerPadding)
  ) {
    val roundedCornerAnim by animatedVisibilityScope.transition
      .animateDp { enterExit ->
        when (enterExit) {
          EnterExitState.PreEnter -> 0.dp
          EnterExitState.Visible -> 12.dp
          EnterExitState.PostExit -> 0.dp
        }
      }

    Card(
      modifier = with(sharedTransitionScope) {
        Modifier
          .fillMaxWidth()
          .height(CollapsedPlayerHeight)
          .sharedBounds(
            sharedContentState = rememberSharedContentState("card"),
            animatedVisibilityScope = animatedVisibilityScope,
            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(roundedCornerAnim)),
            enter = fadeIn(tween(1000)),
            exit = fadeOut(tween(1000))
          )
      },
      colors = CardDefaults.cardColors(containerColor = containerColor),
      onClick = onExpandClick,
      elevation = CardDefaults.elevatedCardElevation(),
      shape = MaterialTheme.shapes.large
    ) {
      Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = scopedAction {  }) {
          Icon(Icons.Default.Pause, null)
        }

        Column(modifier = Modifier.weight(1f)) {
          ProvideContentColorTextStyle(
            MaterialTheme.colorScheme.onPrimary,
            MaterialTheme.typography.labelLarge
          ) {
            Text("Geiles Leben")
          }

          ProvideContentColorTextStyle(
            MaterialTheme.colorScheme.onPrimary,
            MaterialTheme.typography.bodySmall
          ) {
            Text("HBZ")
          }
        }
      }
    }
  }
}

@Composable fun ExpandedPlayer(
  containerColor: Color,
  animatedVisibilityScope: AnimatedVisibilityScope,
  sharedTransitionScope: SharedTransitionScope,
  onCollapseClick: () -> Unit
) {
  val roundedCornerAnim by animatedVisibilityScope.transition
    .animateDp { enterExit ->
      when (enterExit) {
        EnterExitState.PreEnter -> 12.dp
        EnterExitState.Visible -> 0.dp
        EnterExitState.PostExit -> 12.dp
      }
    }
  Card(
    modifier = with(sharedTransitionScope) {
      Modifier
        .fillMaxSize()
        .sharedBounds(
          sharedContentState = rememberSharedContentState("card"),
          animatedVisibilityScope = animatedVisibilityScope,
          clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(roundedCornerAnim)),
          enter = fadeIn(tween(1000)),
          exit = fadeOut(tween(1000))
        )
    },
    colors = CardDefaults.cardColors(containerColor = containerColor),
    onClick = onCollapseClick,
    elevation = CardDefaults.elevatedCardElevation(),
    shape = MaterialTheme.shapes.large
  ) {

  }
}
