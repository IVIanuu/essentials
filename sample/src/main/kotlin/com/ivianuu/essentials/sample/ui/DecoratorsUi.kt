/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

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
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyScope
import com.ivianuu.essentials.ui.navigation.KeyUiDecorator
import com.ivianuu.essentials.ui.navigation.LocalKeyElements
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element

@Provide val decoratorsHomeItem = HomeItem("Decorators") { DecoratorsKey() }

class DecoratorsKey : Key<Unit>

@Provide val decoratorsUi = SimpleKeyUi<DecoratorsKey> {
  SimpleListScreen("Decorators") {
    (1..10).forEach { itemIndex ->
      item {
        ListItem(title = { Text("Item $itemIndex") })
      }
    }
  }
}

fun interface SampleListDecorator : ListDecorator

@Provide val sampleListDecorator = SampleListDecorator {
  item(null) {
    val key = catch { LocalKeyElements.current }.getOrNull()
      ?.element<SampleDecoratorComponent>()?.key
    if (key is DecoratorsKey)
      Text("Sample decorator before content $key")
  }

  content()

  item(null) {
    val key = catch { LocalKeyElements.current }.getOrNull()
      ?.element<SampleDecoratorComponent>()?.key
    if (key is DecoratorsKey)
      Text("Sample decorator after content $key")
  }
}

fun interface SampleKeyUiDecorator : KeyUiDecorator

@Provide val sampleKeyUiDecorator = SampleKeyUiDecorator decorator@ { content ->
  val key = LocalKeyElements.current.element<SampleDecoratorComponent>().key
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

@Provide @Element<KeyScope>
data class SampleDecoratorComponent(val key: Key<*>)
