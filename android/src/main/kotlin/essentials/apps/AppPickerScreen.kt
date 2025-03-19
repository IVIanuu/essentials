/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.apps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.*
import coil.compose.*
import essentials.compose.*
import essentials.coroutines.*
import essentials.resource.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*

class AppPickerScreen(
  val appPredicate: AppPredicate = DefaultAppPredicate,
  val title: String? = null,
) : Screen<AppInfo>

@Provide @Composable fun AppPickerUi(
  coroutineContexts: CoroutineContexts,
  getInstalledApps: getInstalledApps,
  context: ScreenContext<AppPickerScreen> = inject,
): Ui<AppPickerScreen> {
  EsScaffold(topBar = { EsAppBar { Text(context.screen.title ?: "Pick an app") } }) {
    ResourceBox(
      produceScopedState(Resource.Idle()) {
        resourceFlow {
          emit(
            getInstalledApps()
              .fastFilter { context.screen.appPredicate.test(it) }
              .groupBy { it.appName.first().uppercase() }
              .toList()
              .sortedBy { it.first }
          )
        }
          .flowOn(coroutineContexts.computation)
          .collect { value = it }
      }.value
    ) { appGroups ->
      EsLazyColumn {
        appGroups.fastForEach { (title, apps) ->
          item { SectionHeader { Text(title.toString()) } }

          itemsIndexed(apps) { index, app ->
            SectionListItem(
              sectionType = sectionTypeOf(index, apps.size, true),
              onClick = scopedAction { popWithResult(app) },
              title = { Text(app.appName) },
              trailing = {
                AsyncImage(
                  modifier = Modifier.size(40.dp),
                  model = AppIcon(packageName = app.packageName),
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
