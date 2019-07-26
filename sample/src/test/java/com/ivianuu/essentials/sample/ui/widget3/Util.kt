package com.ivianuu.essentials.sample.ui.widget3

import com.ivianuu.essentials.sample.ui.widget3.core.BuildContext
import com.ivianuu.essentials.sample.ui.widget3.core.BuildOwner
import com.ivianuu.essentials.sample.ui.widget3.core.Element
import com.ivianuu.essentials.sample.ui.widget3.core.Widget

class TestWidget(
    key: Any? = null,
    children: (BuildContext.() -> Unit)? = null
) : Widget(key, children) {
    override fun createElement(): Element = TestElement(this)
}


class TestElement(widget: Widget) : Element(widget)

class TestBuildOwner : BuildOwner()