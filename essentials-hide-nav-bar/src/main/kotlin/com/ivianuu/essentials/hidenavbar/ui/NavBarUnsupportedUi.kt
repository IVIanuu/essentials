package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.material.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

object NavBarUnsupportedKey : DialogKey<Nothing>

@Provide val navBarUnsupportedUi: ModelKeyUi<NavBarUnsupportedKey, NavBarUnsupportedModel> = {
  DialogScaffold {
    Dialog(
      title = {
        Text(stringResource(R.string.es_nav_bar_unsupported_title))
      },
      content = {
        Text(stringResource(R.string.es_nav_bar_unsupported_content))
      },
      buttons = {
        TextButton(onClick = model.openMoreInfos) {
          Text(stringResource(R.string.es_more_infos))
        }

        TextButton(onClick = model.close) {
          Text(stringResource(R.string.es_close))
        }
      }
    )
  }
}

@Optics data class NavBarUnsupportedModel(
  val openMoreInfos: () -> Unit = {},
  val close: () -> Unit = {}
)

@Provide fun navBarUnsupportedModel(
  key: NavBarUnsupportedKey,
  navigator: Navigator,
  scope: InjectCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<NavBarUnsupportedModel> = scope.state(
  NavBarUnsupportedModel()
) {
  action(NavBarUnsupportedModel.openMoreInfos()) {
    navigator.push(
      UrlKey(
        "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
      )
    )
  }
  action(NavBarUnsupportedModel.close()) { navigator.pop(key) }
}
