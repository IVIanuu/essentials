/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import com.ivianuu.essentials.ui.animation.transition.FadeScaleStackTransition
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiOptions
import com.ivianuu.essentials.ui.navigation.KeyUiOptionsFactory
import com.ivianuu.injekt.Provide

interface DialogKey<T> : Key<T>

@Provide fun <T : DialogKey<*>> dialogKeyUiOptionsFactory() = KeyUiOptionsFactory<T> {
  KeyUiOptions(opaque = true, transition = FadeScaleStackTransition())
}
