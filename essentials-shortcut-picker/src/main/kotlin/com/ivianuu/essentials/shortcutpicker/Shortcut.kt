/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shortcutpicker

import android.content.*
import android.graphics.drawable.*

data class Shortcut(val intent: Intent, val name: String, val icon: Drawable)
