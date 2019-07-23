/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui.widget3

/*

interface CompositionContext {
    fun emit(component: Component)
    operator fun Component.unaryPlus() {
        emit(this)
    }
}

interface Component {
}

typealias Children = CompositionContext.() -> Unit

fun CompositionContext.VerticalScroller(child: Children): Component = object : Component {
}

fun CompositionContext.Button(text: String): Component = object : Component {
}

fun CompositionContext.LinearLayout(child: Children): Component = object : Component {
}


fun Toolbar(
    leading: Children? = null,
    trailing: Children? = null,
    text: Children? = null
) = object : Component {

}

fun ToolbarIcon(iconRes: Int) = object : Component{}

fun CompositionContext.App() {
    +LinearLayout {
        +Toolbar(
            leading = {
                +ToolbarIcon(R.drawable.icon_back_white)
            },
            trailing = {
                +ToolbarIcon(R.drawable.es_ic_torch_on)
                +ToolbarIcon(R.drawable.es_ic_link)
            }
        )

        +VerticalScroller {
            +Button(text = "hello")
        }
    }
}*/