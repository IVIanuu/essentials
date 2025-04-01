package essentials.sample.ui

import androidx.activity.compose.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.overlay.*
import injekt.*

@Provide val SharedElementHomeItem = HomeItem(
  title = "Shared element",
  { index, color -> SharedElementScreen(index, color) }
) { SharedElementScreen(-1, it) }

class SharedElementScreen(val index: Int, val color: Color) : Screen<Unit>

@Provide @Composable fun SharedElementUi(
  context: ScreenContext<SharedElementScreen> = inject
): Ui<SharedElementScreen> {
  EsScaffold(/*topBar = { EsAppBar { Text("Shared Elements") } }*/) {
    var selectedColor by remember { mutableStateOf<Color?>(null) }
    BackHandler(selectedColor != null) { selectedColor = null }

    SharedTransitionLayout(modifier = Modifier.fillMaxSize(),) {
      val stateHolder = rememberSaveableStateHolder()

      val Null = remember { Any() }

      AnimatedContent(
        selectedColor,
        transitionSpec = { fadeIn(tween(2000)) togetherWith fadeOut(tween(2000)) }
      ) { currentSelectedColor ->
        stateHolder.SaveableStateProvider(currentSelectedColor ?: Null) {
          if (currentSelectedColor != null) {
            DetailContent(currentSelectedColor, this) { selectedColor = null }
          } else {
            ListContent(this) { selectedColor = it }
          }
        }
      }
    }
  }
}

@Composable fun SharedTransitionScope.DetailContent(
  color: Color,
  animatedVisibilityScope: AnimatedVisibilityScope,
  onDismissRequest: () -> Unit
) {
  Box(
    modifier = Modifier
      .sharedBounds(
        sharedContentState = rememberSharedContentState("container_$color"),
        animatedVisibilityScope = animatedVisibilityScope,
        enter = fadeIn(tween(2000)),
        exit = fadeOut(tween(2000)),
        boundsTransform = { _, _ -> tween(2000) },
        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
      )
      .fillMaxSize()
      .background(SectionDefaults.colors().containerColor)
  ) {
    Box(
      modifier = Modifier
        .sharedElement(
          sharedContentState = rememberSharedContentState(color.toString()),
          animatedVisibilityScope = animatedVisibilityScope,
          boundsTransform = { _, _ -> tween(2000) }
        )
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(color)
        .clickable(onClick = onDismissRequest)
    )
  }
}

@Composable fun SharedTransitionScope.ListContent(
  animatedVisibilityScope: AnimatedVisibilityScope,
  showColor: (Color) -> Unit
) {
  EsLazyColumn(modifier = Modifier.fillMaxSize()) {
    DefaultColorPalette.forEach { color ->
      item(color) {
        SectionListItem(
          modifier = Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState("container_$color"),
            animatedVisibilityScope = animatedVisibilityScope,
            enter = fadeIn(tween(2000)),
            exit = fadeOut(tween(2000)),
            boundsTransform = { _, _ -> tween(2000) },
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
          ),
          title = { Text("Title") },
          trailing = {
            Box(
              modifier = Modifier
                .sharedElement(
                  sharedContentState = rememberSharedContentState(color.toString()),
                  animatedVisibilityScope = animatedVisibilityScope,
                  boundsTransform = { _, _ -> tween(2000) }
                )
                .size(100.dp)
                .background(color)
                .clickable { showColor(color) }
            )
          }
        )
      }
    }
  }
}
