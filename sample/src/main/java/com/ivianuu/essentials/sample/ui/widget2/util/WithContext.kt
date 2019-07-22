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

package com.ivianuu.essentials.sample.ui.widget2.util

import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.Element
import com.ivianuu.essentials.sample.ui.widget2.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.essentials.util.cast

open class WithContext(
    key: Any? = null,
    val child: (BuildContext) -> Widget
) : StatelessWidget(key) {
    override fun build(context: BuildContext): Widget = child(context)
}

open class WithParentElement(
    key: Any? = null,
    val child: (Element) -> Widget
) : StatelessWidget(key) {
    override fun build(context: BuildContext): Widget = child(context.cast())
}