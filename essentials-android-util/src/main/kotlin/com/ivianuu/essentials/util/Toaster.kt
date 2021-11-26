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
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.coroutines.MainDispatcher

fun interface Toaster : (String) -> Unit

@Provide fun toaster(
  context: AppContext,
  mainDispatcher: MainDispatcher,
  scope: NamedCoroutineScope<AppScope>
) = Toaster { message ->
  launch(mainDispatcher) {
    Toast.makeText(
      context,
      message,
      Toast.LENGTH_SHORT
    ).show()
  }
}

@Provide data class ToastContext(
  @Provide val toaster: Toaster,
  @Provide val resourceProvider: ResourceProvider
)

fun showToast(message: String, @Inject toaster: Toaster) {
  toaster(message)
}

fun showToast(messageRes: Int, @Inject T: Toaster, RP: ResourceProvider) {
  showToast(message = loadResource(messageRes))
}

fun showToast(messageRes: Int, vararg args: Any?, @Inject T: Toaster, RP: ResourceProvider) {
  showToast(message = loadResource(messageRes, *args))
}
