/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

/**
 * Nav bar rotation behavior
 */
enum class NavBarRotationMode(val titleRes: Int) {
  MARSHMALLOW(R.string.es_nav_bar_rotation_mode_marshmallow),
  NOUGAT(R.string.es_nav_bar_rotation_mode_nougat),
  TABLET(R.string.es_nav_bar_rotation_mode_tablet)
}
