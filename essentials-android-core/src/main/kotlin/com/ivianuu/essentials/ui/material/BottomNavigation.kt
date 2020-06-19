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

package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.emptyContent
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.layout.RowScope
import androidx.ui.material.BottomNavigation
import androidx.ui.material.BottomNavigationItem
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.contentColorFor
import androidx.ui.material.primarySurface
import androidx.ui.savedinstancestate.rememberSavedInstanceState
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
class BottomNavigationController<T>(
    items: List<T>,
    initial: T = items.first()
) {
    var items by mutableStateOf(items)
    var selectedItem by mutableStateOf(initial)
}

@Composable
fun <T> ProvideBottomNavigationController(
    items: List<T>,
    initial: T = items.first(),
    children: @Composable () -> Unit
) {
    val controller = rememberSavedInstanceState(items) {
        BottomNavigationController(items = items, initial = initial)
    }

    Providers(BottomNavigationControllerAmbient provides controller, children = children)
}

@Composable
fun <T> ProvideBottomNavigationController(
    controller: BottomNavigationController<T>,
    children: @Composable () -> Unit
) {
    Providers(BottomNavigationControllerAmbient provides controller, children = children)
}

private val BottomNavigationControllerAmbient =
    staticAmbientOf<BottomNavigationController<*>> {
        error("No bottom navigation controller found")
    }

@Composable
fun <T> ambientBottomNavigationController(): BottomNavigationController<T> = BottomNavigationControllerAmbient.current as BottomNavigationController<T>

private val BottomNavigationItemAmbient =
    staticAmbientOf<Any?>()

@Composable
fun <T> ambientBottomNavigationItem(): T = BottomNavigationItemAmbient.current as T

@Composable
fun <T> BottomNavigation(
    controller: BottomNavigationController<T> = ambientBottomNavigationController(),
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 8.dp,
    item: @Composable RowScope.(T) -> Unit
) {
    BottomNavigation(
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation
    ) {
        controller.items.forEach { item ->
            key(item) {
                Providers(BottomNavigationItemAmbient provides item) {
                    item(item)
                }
            }
        }
    }
}

@Composable
fun RowScope.BottomNavigationItem(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit = emptyContent(),
    modifier: Modifier = Modifier,
    alwaysShowLabels: Boolean = true,
    activeColor: Color = contentColor(),
    inactiveColor: Color = EmphasisAmbient.current.medium.applyEmphasis(activeColor)
) {
    val controller = ambientBottomNavigationController<Any?>()
    val item = ambientBottomNavigationItem<Any?>()
    BottomNavigationItem(
        icon = icon,
        text = text,
        modifier = modifier,
        alwaysShowLabels = alwaysShowLabels,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        selected = controller.selectedItem == item,
        onSelected = { controller.selectedItem = item }
    )
}

@Composable
fun <T> BottomNavigationContent(
    bottomNavigationController: BottomNavigationController<T> = ambientBottomNavigationController(),
    modifier: Modifier = Modifier,
    transition: StackTransition = FadeStackTransition(),
    item: @Composable (T) -> Unit
) {
    AnimatedBox(
        modifier = modifier,
        current = bottomNavigationController.selectedItem,
        transition = transition
    ) {
        Providers(BottomNavigationItemAmbient provides it) {
            item(it)
        }
    }
}
