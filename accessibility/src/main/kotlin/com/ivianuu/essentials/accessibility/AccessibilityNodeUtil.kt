package com.ivianuu.essentials.accessibility

import android.view.accessibility.*

fun AccessibilityNodeInfo.firstNodeOrNull(
  predicate: (AccessibilityNodeInfo) -> Boolean
): AccessibilityNodeInfo? {
  if (predicate(this)) return this

  (0 until childCount)
    .forEach {
      getChild(it).firstNodeOrNull(predicate)
        ?.let { return it }
    }

  return null
}
