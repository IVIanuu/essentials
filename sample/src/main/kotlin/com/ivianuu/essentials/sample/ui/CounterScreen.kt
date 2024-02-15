/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide

@Provide val counterHomeItem = HomeItem("Counter") { CounterScreen() }

class CounterScreen : Screen<Unit>

@Provide val counterUi = Ui<CounterScreen, CounterState> { state ->
  ScreenScaffold(topBar = { AppBar { Text("Counter") } }) {
    Column(
      modifier = Modifier.center(),
      verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = "Count: ${state.count}", style = MaterialTheme.typography.h3)
      ExtendedFloatingActionButton(onClick = state.inc, text = { Text("Inc") })
      ExtendedFloatingActionButton(onClick = state.dec, text = { Text("dec") })
    }
  }
}

data class CounterState(val count: Int, val inc: () -> Unit, val dec: () -> Unit)

@Provide fun counterPresenter(toaster: Toaster) = Presenter {
  var count by remember { mutableStateOf(0) }
  CounterState(
    count = count,
    inc = action { count++ },
    dec = action {
      if (count > 0) count--
      else toaster("Value cannot be less than 0!")
    }
  )
}
