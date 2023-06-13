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
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide

@Provide val counterHomeItem = HomeItem("Counter") { CounterKey() }

class CounterKey : Key<Unit>

@Provide val counterUi = Ui<CounterKey, CounterModel> { model ->
  Scaffold(
    topBar = { TopAppBar(title = { Text("Counter") }) }
  ) {
    Column(
      modifier = Modifier.center(),
      verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = "Count: ${model.count}", style = MaterialTheme.typography.h3)
      ExtendedFloatingActionButton(onClick = model.inc, text = { Text("Inc") })
      ExtendedFloatingActionButton(onClick = model.dec, text = { Text("dec") })
    }
  }
}

data class CounterModel(val count: Int, val inc: () -> Unit, val dec: () -> Unit)

@Provide fun counterModel(toaster: Toaster) = Model {
  var count by remember { mutableStateOf(0) }
  CounterModel(
    count = count,
    inc = action { count++ },
    dec = action {
      if (count > 0) count--
      else toaster("Value cannot be less than 0!")
    }
  )
}
