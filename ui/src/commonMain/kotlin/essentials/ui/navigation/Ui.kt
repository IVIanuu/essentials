/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.navigation

import injekt.Tag

@Tag annotation class UiTag<S : Screen<*>>
typealias Ui<S> = @UiTag<S> Unit
