/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import com.ivianuu.essentials.help.HelpCategory
import com.ivianuu.essentials.help.HelpItem
import com.ivianuu.essentials.help.HelpKey
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.injekt.Provide

@Provide val helpHomeItem = HomeItem("Help") { HelpKey }

@Provide val helpCategories: List<HelpCategory> get() = listOf(
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
