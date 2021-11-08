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

package com.ivianuu.essentials.util

import android.widget.Toast
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Inject2
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.coroutines.ComponentScope
import com.ivianuu.injekt.coroutines.MainDispatcher

@Tag annotation class ToasterTag
typealias Toaster = @ToasterTag (String) -> Unit

@Provide fun toaster(
  context: AppContext,
  mainDispatcher: MainDispatcher,
  scope: ComponentScope<AppComponent>
): Toaster = { message ->
  launch(mainDispatcher) {
    Toast.makeText(
      context,
      message,
      Toast.LENGTH_SHORT
    ).show()
  }
}

typealias Toasts = Inject2<Toaster, ResourceProvider>

fun showToast(message: String, @Inject toaster: Toaster) {
  toaster(message)
}

@Toasts fun showToast(messageRes: Int) {
  showToast(message = loadResource(messageRes))
}

@Toasts fun showToast(messageRes: Int, vararg args: Any?) {
  showToast(message = loadResource(messageRes, *args))
}
