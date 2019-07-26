package com.ivianuu.essentials.sample.ui.widget3.android

import com.ivianuu.essentials.sample.ui.widget3.core.Element

fun Element.getNearestViewElement(): ViewElement =
    findRecursive { it is ViewElement } as? ViewElement ?: error("couldn't find child")

fun Element.findRecursive(predicate: (Element) -> Boolean): Element? {
    if (predicate(this)) return this
    children?.forEach {
        if (predicate(it)) return it
    }
    return null
}