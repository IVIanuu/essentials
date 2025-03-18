/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.material3.*
import essentials.compose.*
import essentials.ui.material.*
import essentials.ui.overlay.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide fun unlockHomeItem(
  screenState: Flow<ScreenState>,
  showToast: showToast,
  turnScreenOn: turnScreenOn,
  unlockScreen: unlockScreen
) = HomeItem("Unlock") {
  BottomSheetScreen {
    Subheader { Text("Unlock") }

    SectionListItem(
      sectionType = SectionType.FIRST,
      onClick = action {
        showToast("Turn the screen off")
        screenState.first { !it.isOn }
        delay(1500)
        val unlocked = unlockScreen()
        showToast("Screen unlocked $unlocked")
      },
      title = { Text("Unlock") }
    )

    SectionListItem(
      sectionType = SectionType.LAST,
      onClick = action {
        showToast("Turn the screen off")
        screenState.first { !it.isOn }
        delay(1500)
        val screenOn = turnScreenOn()
        showToast("Screen activated $screenOn")
      },
      title = { Text("Activate") }
    )
  }
}
