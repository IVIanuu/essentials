/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import com.ivianuu.essentials.help.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.injekt.*

@Provide val helpHomeItem = HomeItem("Help") {
  HelpScreen(
    listOf(
      HelpCategory(
        title = null,
        items = listOf(
          HelpItem(
            question = "What's the answer to everything?",
            answer = "42"
          ),
          HelpItem(
            question = "Sample question 1",
            answer = "Sample answer 1",
            actions = {
              TextButton(onClick = {}) {
                Text("Action 1")
              }

              TextButton(onClick = {}) {
                Text("Action 2")
              }
            }
          ),
          HelpItem(
            question = "Sample question 2",
            answer = "Sample answer 2",
            actions = {
              TextButton(onClick = {}) {
                Text("Action 1")
              }
            }
          ),
          HelpItem(
            question = "Sample question 3",
            answer = "Sample answer 3 fkgfaw  fka fl alf la lf yls vls dlv yklvlly vldy vl yld vlyd vl dlv ly ly vl yl vly lv ylv lyd vldy vl dyl dv"
          ),
          HelpItem(
            question = "Sample question 4",
            answer = "Sample answer 4 fkgfaw  fka fl alf la lf yls vls dlv yklvlly vldy vl yld vlyd vl dlv ly ly vl yl vly lv ylv lyd vldy vl dyl dv"
          )
        )
      )
    )
  )
}
