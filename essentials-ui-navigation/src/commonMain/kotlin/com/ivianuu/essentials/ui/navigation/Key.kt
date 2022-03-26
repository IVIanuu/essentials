/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.injekt.*

interface Key<T>

interface RootKey : Key<Unit>

interface PopupKey<T> : Key<T>

interface CriticalUserFlowKey<T> : Key<T>

@Provide fun <T : PopupKey<*>> popupKeyUiOptionsFactory() = KeyUiOptionsFactory<T> {
  KeyUiOptions(opaque = true, transition = FadeScaleStackTransition())
}
