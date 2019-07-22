/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.sample.ui.widget2.sample

import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget2.es.WidgetController
import com.ivianuu.essentials.sample.ui.widget2.layout.LinearLayoutWidget
import com.ivianuu.essentials.sample.ui.widget2.layout.ScrollViewWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.essentials.sample.ui.widget2.material.MaterialButtonWidget
import com.ivianuu.essentials.sample.ui.widget2.view.Avatar
import com.ivianuu.essentials.sample.ui.widget2.view.Margin
import com.ivianuu.essentials.sample.ui.widget2.view.MatchParent
import com.ivianuu.essentials.util.dp

class WidgetController2 : WidgetController() {

    override fun build(context: BuildContext): Widget {
        return LinearLayoutWidget(
            children = listOf(
                SimpleTextToolbar(title = "Compose"),
                MatchParent(
                    child = ScrollViewWidget(
                        child = MatchParent(
                            LinearLayoutWidget(
                                children = (1..100).map { i ->
                                    ListItem(
                                        title = "Title $i",
                                        text = "TextViewWidget $i",
                                        primaryAction = Margin(
                                            left = dp(16).toInt(),
                                            child = Avatar(
                                                avatarRes = R.drawable.es_ic_torch_on
                                            )
                                        ),
                                        secondaryAction = Margin(
                                            right = dp(16).toInt(),
                                            child = MaterialButtonWidget(
                                                text = "Click me",
                                                onClick = { }
                                            )
                                        ),
                                        onClick = { }
                                    )
                                }
                            )
                        )
                    )
                )
            )
        )
    }
}
