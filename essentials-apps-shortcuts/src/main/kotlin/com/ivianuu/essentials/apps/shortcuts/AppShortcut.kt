/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
package com.ivianuu.essentials.apps.shortcuts

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable

data class AppShortcut(
  val id: String,
  val intent: Intent,
  val packageName: String,
  val activity: ComponentName,
  val shortLabel: String,
  val longLabel: String?,
  val disabledMessage: String?,
  val icon: Drawable
)
