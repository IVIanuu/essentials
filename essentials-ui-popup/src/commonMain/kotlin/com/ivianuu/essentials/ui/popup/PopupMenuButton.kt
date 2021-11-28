/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.LocalElements
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import kotlinx.coroutines.launch

@Composable fun PopupMenuButton(
  items: List<PopupMenu.Item>,
  onCancel: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = Modifier
      .size(size = 40.dp)
      .popupClickable(
        items = items,
        onCancel = onCancel,
        indication = rememberRipple(bounded = false)
      )
      .then(modifier),
    contentAlignment = Alignment.Center
  ) {
    Icon(Icons.Default.MoreVert, null)
  }
}

@Composable fun Modifier.popupClickable(
  items: List<PopupMenu.Item>,
  onCancel: (() -> Unit)? = null,
  indication: Indication = LocalIndication.current,
) = composed {
  val component = LocalElements.current<PopupMenuButtonComponent>()

  var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

  val scope = rememberCoroutineScope()
  onGloballyPositioned { coordinates = it }
    .clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = indication
    ) {
      scope.launch {
        component.navigator.push(
          PopupKey(
            position = coordinates!!.boundsInRoot(),
            onCancel = onCancel
          ) {
            PopupMenu(items = items)
          }
        )
      }
    }
}

@Provide @Element<UiScope>
data class PopupMenuButtonComponent(val navigator: Navigator)
