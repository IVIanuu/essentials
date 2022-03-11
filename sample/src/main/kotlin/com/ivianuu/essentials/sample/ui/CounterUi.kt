/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

@Provide val counterHomeItem = HomeItem("Counter") { CounterKey }

object CounterKey : Key<Unit>

@Provide val counterUi = ModelKeyUi<CounterKey, CounterModel> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Counter") }) }
  ) {
    Column(
      modifier = Modifier.center(),
      verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Count: ${model.count}",
        style = MaterialTheme.typography.h3
      )

      ExtendedFloatingActionButton(
        text = { Text("Inc") },
        onClick = model.inc
      )

      ExtendedFloatingActionButton(
        text = { Text("dec") },
        onClick = model.dec
      )
    }
  }
}

data class CounterModel(val count: Int, val inc: () -> Unit, val dec: () -> Unit)

@Provide fun counterModel(T: ToastContext) = Model {
  var count by remember { mutableStateOf(0) }
  CounterModel(
    count = count,
    inc = action { count++ },
    dec = action {
      if (count > 0) count--
      else showToast("Value cannot be less than 0!")
    }
  )
}
