/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.material3.*
import essentials.*
import essentials.compose.*
import essentials.gestures.action.*
import essentials.gestures.action.ui.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.overlay.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Provide fun actionsHomeItem(
  actions: Actions,
  executeAction: executeAction,
  showToast: showToast,
  ctx: ScreenContext<*> = inject
) = HomeItem("Actions") {
  BottomSheetScreen {
    Subheader { Text("Actions") }

    SectionListItem(
      sectionType = SectionType.FIRST,
      onClick = scopedAction {
        val actionId = navigator().push(ActionPickerScreen())
          .safeAs<ActionPickerScreen.Result.Action>()
          ?.actionId ?: return@scopedAction

        val action = actions.get(actionId)

        delay(1.seconds)

        showToast("Execute action ${action.title}")

        executeAction(actionId)
      },
      title = { Text("Execute action") }
    )

    SectionListItem(
      sectionType = SectionType.LAST,
      colors = SectionDefaults.colors(tone = Tone.NEGATIVE),
      onClick = scopedAction {
        val actionId = navigator().push(ActionPickerScreen())
          .safeAs<ActionPickerScreen.Result.Action>()
          ?.actionId ?: return@scopedAction

        val action = actions.get(actionId)

        delay(1.seconds)

        while (true) {
          showToast("Execute action ${action.title}")

          executeAction(actionId)

          delay(3.seconds)
        }
      },
      title = { Text("Loop action") }
    )
  }
}
