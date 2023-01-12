/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.injekt.Provide

@Provide val textInputHomeItem = HomeItem("Text input") { TextInputKey }

object TextInputKey : Key<Unit>

@Provide val textInputUi = SimpleKeyUi<TextInputKey> {
  val state = remember { TextInputState() }

  if (!state.searchVisible) {
    state.inputValue = TextFieldValue()
  }

  val items = AllItems.filter {
    state.inputValue.text.isEmpty() || state.inputValue.text in it.toLowerCase().trim()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          if (state.searchVisible) {
            val focusRequester = remember { FocusRequester() }

            Box(
              modifier = Modifier.fillMaxSize()
                .clickable { focusRequester.requestFocus() }
            ) {
              if (state.inputValue.text.isEmpty()) {
                Text(
                  text = "Search..",
                  style = MaterialTheme.typography.subtitle1,
                  modifier = Modifier.alpha(0.5f)
                    .align(Alignment.CenterStart)
                )
              }
              BasicTextField(
                value = state.inputValue,
                onValueChange = { state.inputValue = it },
                textStyle = MaterialTheme.typography.subtitle1,
                modifier = Modifier.align(Alignment.CenterStart)
                  .focusRequester(focusRequester)
              )
            }

            DisposableEffect(true) {
              focusRequester.requestFocus()
              onDispose { }
            }
          } else {
            Text("Text input")
          }
        }
      )
    },
    floatingActionButton = {
      if (!state.searchVisible) {
        ExtendedFloatingActionButton(
          text = { Text("Search") },
          onClick = { state.searchVisible = true }
        )
      }
    }
  ) {
    if (items.isNotEmpty()) {
      /*val animationClock = LocalAnimationClock.current
      val flingConfig = FlingConfig()
      val scrollerPosition = retain(items) { ScrollerPosition() }

      val lastScrollPosition = holderFor(scrollerPosition) { scrollerPosition.value }

      if (lastScrollPosition.value < scrollerPosition.value) {
          //keyboardManager.hideKeyboard()
          if (state.searchVisible && state.inputValue.text.isEmpty()) {
              state.searchVisible = false
          }
      }*/

      VerticalList {
        items(items) { item ->
          ListItem(title = { Text(item) })
        }
      }
    } else {
      Text(
        text = "No results",
        modifier = Modifier.center()
      )
    }
  }
}

private class TextInputState {
  var searchVisible by mutableStateOf(false)
  var inputValue by mutableStateOf(TextFieldValue())
}

private val AllItems = (0..100).map { "Item: $it" }
