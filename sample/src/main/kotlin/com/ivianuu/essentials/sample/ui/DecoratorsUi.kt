/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*

@Provide val decoratorsHomeItem = HomeItem("Decorators") { DecoratorsKey }

object DecoratorsKey : Key<Unit>

@Provide val decoratorsUi = KeyUi<DecoratorsKey> {
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
    val key = runCatching { LocalKeyUiElements.current }.getOrElse { null }
      ?.invoke<SampleDecoratorComponent>()?.key
    if (key is DecoratorsKey)
      Text("Sample decorator before content $key")
  }

  content()

  item(null) {
    val key = runCatching { LocalKeyUiElements.current }.getOrElse { null }
      ?.invoke<SampleDecoratorComponent>()?.key
    if (key is DecoratorsKey)
      Text("Sample decorator before content $key")
  }
}

fun interface SampleKeyUiDecorator : KeyUiDecorator

@Provide val sampleKeyUiDecorator = SampleKeyUiDecorator decorator@ { content ->
  val key = LocalKeyUiElements.current<SampleDecoratorComponent>().key
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

@Provide @Element<KeyUiScope>
data class SampleDecoratorComponent(val key: Key<*>)
