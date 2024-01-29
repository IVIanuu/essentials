/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shortcutpicker

import android.content.Intent
import android.graphics.drawable.Drawable

data class Shortcut(val intent: Intent, val name: String, val icon: Drawable)
