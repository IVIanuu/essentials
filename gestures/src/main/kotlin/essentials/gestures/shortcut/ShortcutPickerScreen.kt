/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.shortcut

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.apps.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*

class ShortcutPickerScreen : Screen<Shortcut>

@Provide @Composable fun ShortcutPickerUi(
  getShortcuts: getShortcuts,
  extractShortcut: extractShortcut,
  context: ScreenContext<ShortcutPickerScreen> = inject,
  showToast: showToast,
  toAppInfo: suspend String.() -> AppInfo?
): Ui<ShortcutPickerScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Pick an shortcut") } }) {
    ResourceBox(
      produceScopedState(Resource.Idle()) {
        value = catchResource {
          getShortcuts()
            .also { println(it) }
            .groupBy { it.packageName }
            .mapKeys { it.key.toAppInfo()!!.appName }
            .toList()
            .sortedBy { it.first.lowercase() }
        }
      }.value
    ) { shortcutGroups ->
      EsLazyColumn {
        shortcutGroups.forEach { (title, shortcuts) ->
          item { SectionHeader { Text(title) } }

          itemsIndexed(shortcuts) { index, shortcut ->
            SectionListItem(
              sectionType = sectionTypeOf(index, shortcuts.size, true),
              onClick = scopedAction {
                catch {
                  val shortcutRequestResult = navigator().push(shortcut.intent.asScreen())
                    ?.getOrNull()
                    ?.data ?: return@catch
                  val finalShortcut: Shortcut = extractShortcut(
                    shortcut.packageName,
                    shortcutRequestResult
                  )
                  popWithResult(finalShortcut)
                }.onFailure {
                  it.printStackTrace()
                  showToast("Failed to pick a shortcut!")
                }
              },
              title = { Text(shortcut.title) },
              trailing = {
                Image(
                  modifier = Modifier.size(40.dp),
                  bitmap = shortcut.icon.toBitmap().asImageBitmap(),
                  contentDescription = null
                )
              }
            )
          }
        }
      }
    }
  }
}
