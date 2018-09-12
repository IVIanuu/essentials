package com.ivianuu.essentials.util.ext

import android.content.res.Resources

inline val Int.dp get() = this * Resources.getSystem().displayMetrics.density