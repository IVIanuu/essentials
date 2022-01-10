/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

interface DialogKey<T> : Key<T>

@Provide fun <T : DialogKey<*>> dialogKeyUiOptionsFactory() = KeyUiOptionsFactory<T> {
  KeyUiOptions(opaque = true, transition = FadeScaleStackTransition())
}
