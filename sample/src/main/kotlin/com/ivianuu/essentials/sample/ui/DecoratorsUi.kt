package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.core.LocalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiDecorator
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.injekt.Provide

@Provide val decoratorsHomeItem = HomeItem("Decorators") { DecoratorsKey }

object DecoratorsKey : Key<Unit>

@Provide val decoratorsUi: KeyUi<DecoratorsKey> = {
  SimpleListScreen("Decorators") {
    (1..10).forEach { itemIndex ->
      item {
        ListItem(title = { Text("Item $itemIndex") })
      }
    }
  }
}

@Provide val sampleListDecorator: ListDecorator = { content ->
  item {
    val key = catch { LocalKeyUiComponent.current }.getOrNull()?.key
    if (key is DecoratorsKey)
      Text("Sample decorator before content $key")
  }

  content()

  item {
    val key = catch { LocalKeyUiComponent.current }.getOrNull()?.key
    if (key is DecoratorsKey)
      Text("Sample decorator before content $key")
  }
}

@Provide val sampleKeyUiDecorator: KeyUiDecorator = decorator@{ content ->
  val key = LocalKeyUiComponent.current.key
  if (key !is DecoratorsKey) {
    content()
    return@decorator
  } else {
    Column {
      Box(modifier = Modifier.weight(1f)) {
        val currentInsets = LocalInsets.current
        CompositionLocalProvider(
          LocalInsets provides currentInsets.copy(bottom = 0.dp),
          content = content
        )
      }

      Surface(color = MaterialTheme.colors.primary, elevation = 8.dp) {
        InsetsPadding(top = false) {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "This is a bottom decorator",
              style = MaterialTheme.typography.h3
            )
          }
        }
      }
    }
  }
}
