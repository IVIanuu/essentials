package essentials.ui.material

import android.annotation.*
import androidx.activity.compose.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import arrow.fx.coroutines.*
import essentials.compose.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.*

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable fun BottomSheet(
  modifier: Modifier = Modifier,
  state: AnchoredDraggableState<BottomSheetValue> = rememberBottomSheetState(),
  onDismissRequest: onBottomSheetDismissRequest = inject,
  dismissOnOutsideTouch: Boolean = true,
  dismissOnBack: Boolean = true,
  animateToExpandedOnInit: Boolean = true,
  content: @Composable ColumnScope.() -> Unit
) {
  val animateToHiddenAndDismiss = action {
    guarantee(
      fa = { state.animateTo(BottomSheetValue.HIDDEN) },
      finalizer = { onDismissRequest() }
    )
  }

  BackHandler(enabled = dismissOnBack, animateToHiddenAndDismiss)

  BackHandler(
    enabled = state.targetValue == BottomSheetValue.EXPANDED &&
    state.anchors.hasAnchorFor(BottomSheetValue.COLLAPSED),
    onBack = action { state.animateTo(BottomSheetValue.COLLAPSED) }
  )

  val overscrollEffect = ScrollableDefaults.overscrollEffect()

  BoxWithConstraints(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.BottomCenter
  ) {
    val scrimAlpha by animateFloatAsState(
      if (state.targetValue != BottomSheetValue.HIDDEN) 1f else 0f
    )
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.scrim.copy(0.32f * scrimAlpha))
        .then(
          if (dismissOnOutsideTouch && state.targetValue != BottomSheetValue.HIDDEN)
            Modifier.clickable(null, null, onClick = animateToHiddenAndDismiss)
          else Modifier
        )
    )

    val maxHeight = constraints.maxHeight
    Box(
      modifier = Modifier
        .onSizeChanged {
          val contentHeight = it.height.toFloat()
          val newAnchors = DraggableAnchors {
            BottomSheetValue.HIDDEN at contentHeight
            if (contentHeight > maxHeight / 2)
              BottomSheetValue.COLLAPSED at contentHeight * 0.5f
            BottomSheetValue.EXPANDED at 0f
          }
          val newTarget = if (newAnchors.hasAnchorFor(state.targetValue))
            state.targetValue
          else newAnchors.closestAnchor(state.offset) ?: state.targetValue
          state.updateAnchors(newAnchors, newTarget)
        }
        .offset {
          try {
            IntOffset(x = 0, y = state.offset.roundToInt())
          } catch (e: Throwable) {
            IntOffset(0, 0)
          }
        }
        .nestedScroll(rememberBottomSheetScrollConnection(state))
        .anchoredDraggable(
          state = state,
          orientation = Orientation.Vertical,
          overscrollEffect = overscrollEffect
        )
        .overscroll(overscrollEffect)
    ) {
      Surface(
        shape = MaterialTheme.shapes.extraLarge.copy(
          bottomStart = CornerSize(0),
          bottomEnd = CornerSize(0)
        ),
        color = MaterialTheme.colorScheme.surfaceContainerLow
      ) {
        Column(
          modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(
              start = 8.dp, end = 8.dp,
              bottom = 8.dp
            )
        ) {

          /*val statusBarPadding = with(LocalDensity.current) {
            WindowInsets.statusBars.getTop(this).toDp()
          }
          val currentStatusBarPadding = lerp(
            0.dp,
            statusBarPadding,
            state.anchoredDraggableState.progress(
              BottomSheetValue.HIDDEN,
              if (state.anchoredDraggableState.anchors.hasAnchorFor(BottomSheetValue.PARTIAL_EXPANDED))
                BottomSheetValue.PARTIAL_EXPANDED else BottomSheetValue.EXPANDED
            )
          )

          Spacer(Modifier.height(currentStatusBarPadding))*/

          BottomSheetDefaults.DragHandle(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurfaceVariant
              .copy(alpha = ContentAlpha.Medium)
          )

          content()
        }
      }
    }
  }

  if (animateToExpandedOnInit)
    LaunchedEffect(true) {
      println("animate to expanded")
      state.animateTo(BottomSheetValue.EXPANDED)
    }

  LaunchedEffect(state) {
    snapshotFlow { state.settledValue }
      .filter { it == BottomSheetValue.EXPANDED }
      .take(1)
      .flatMapLatest { snapshotFlow { state.settledValue } }
      .filter { it == BottomSheetValue.HIDDEN }
      .take(1)
      .collect { onDismissRequest() }
  }
}

enum class BottomSheetValue { EXPANDED, COLLAPSED, HIDDEN }

@Composable fun rememberBottomSheetScrollConnection(
  state: AnchoredDraggableState<BottomSheetValue>
): NestedScrollConnection {
  val scope = rememberCoroutineScope()
  return remember {
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        return if (delta < 0 && source == NestedScrollSource.UserInput) {
          Offset(x = 0f, y = state.dispatchRawDelta(delta))
        } else {
          Offset.Zero
        }
      }

      override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
      ): Offset = if (source == NestedScrollSource.UserInput) {
        Offset(x = 0f, y = state.dispatchRawDelta(available.y))
      } else {
        Offset.Zero
      }

      override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.y
        val currentOffset = state.requireOffset()
        val minAnchor = state.anchors.minAnchor()
        return if (toFling < 0 && currentOffset > minAnchor) {
          onFling(toFling)
          // since we go to the anchor with tween settling, consume all for the best UX
          available
        } else {
          Velocity.Zero
        }
      }

      override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        onFling(available.y)
        return available
      }

      private fun onFling(y: Float) {
        scope.launch { state.settle(y) }
      }
    }
  }
}

@Composable fun rememberBottomSheetState(
  initialState: BottomSheetValue = BottomSheetValue.HIDDEN
): AnchoredDraggableState<BottomSheetValue> {
  val density = LocalDensity.current
  return rememberSaveable {
    AnchoredDraggableState<BottomSheetValue>(
      initialValue = initialState,
      positionalThreshold = { with(density) { 56.dp.toPx() } },
      velocityThreshold = { with(density) { 125.dp.toPx() } },
      snapAnimationSpec = spring(),
      decayAnimationSpec = splineBasedDecay(density)
    )
  }
}

@Tag typealias onBottomSheetDismissRequest = () -> Unit
@Provide @Composable fun defaultOnBottomSheetDismissRequest(
  navigator: Navigator = inject,
  screen: Screen<*> = inject
): onBottomSheetDismissRequest = action { navigator.pop(screen) }
