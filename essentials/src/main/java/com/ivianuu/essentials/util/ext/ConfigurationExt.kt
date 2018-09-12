package com.ivianuu.essentials.util.ext

import android.content.res.Configuration
import android.view.View

inline val Configuration.isRtl: Boolean
    get() = layoutDirection == View.LAYOUT_DIRECTION_RTL

inline val Configuration.isLtr: Boolean
    get() = layoutDirection == View.LAYOUT_DIRECTION_LTR

inline val Configuration.isPortrait: Boolean
    get() = orientation == Configuration.ORIENTATION_PORTRAIT

inline val Configuration.isLandscape: Boolean
    get() = orientation == Configuration.ORIENTATION_LANDSCAPE