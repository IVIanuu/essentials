/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.resource

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.core.localHorizontalInsets
import com.ivianuu.essentials.ui.core.localVerticalInsets
import com.ivianuu.essentials.ui.layout.center

@Composable
fun <T> ResourceLazyColumnFor(
    resource: Resource<List<T>>,
    modifier: Modifier = Modifier,
    transition: StackTransition = ResourceBoxDefaults.transition,
    error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
    loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
    idle: @Composable () -> Unit = {},
    successEmpty: @Composable () -> Unit = {},
    successItemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    ResourceBox(
        resource = resource,
        modifier = modifier,
        transition = transition,
        error = error,
        loading = loading,
        idle = idle
    ) { items ->
        if (items.isNotEmpty()) {
            LazyColumn(
                contentPadding = localVerticalInsets()
            ) { items(items) { successItemContent(it) } }
        } else {
            successEmpty()
        }
    }
}

@Composable
fun <T> ResourceLazyRowFor(
    resource: Resource<List<T>>,
    modifier: Modifier = Modifier,
    transition: StackTransition = ResourceBoxDefaults.transition,
    error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
    loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
    idle: @Composable () -> Unit = {},
    successEmpty: @Composable () -> Unit = {},
    successItemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    ResourceBox(
        resource = resource,
        modifier = modifier,
        transition = transition,
        error = error,
        loading = loading,
        idle = idle
    ) { items ->
        if (items.isNotEmpty()) {
            LazyRow(contentPadding = localHorizontalInsets()) {
                items(items) { successItemContent(it) }
            }
        } else {
            successEmpty()
        }
    }
}

@Composable
fun <T> ResourceBox(
    resource: Resource<T>,
    modifier: Modifier = Modifier,
    transition: StackTransition = ResourceBoxDefaults.transition,
    error: @Composable (Throwable) -> Unit = ResourceBoxDefaults.error,
    loading: @Composable () -> Unit = ResourceBoxDefaults.loading,
    idle: @Composable () -> Unit = {},
    success: @Composable (T) -> Unit
) {
    // we only wanna animate if the resource type has changed
    val resourceState = remember(resource::class) { mutableStateOf(resource) }
    resourceState.value = resource

    AnimatedBox(
        current = resourceState,
        modifier = modifier,
        transition = transition
    ) { currentState ->
        when (val currentValue = currentState.value) {
            is Idle -> idle()
            is Loading -> loading()
            is Success -> success(currentValue.value)
            is Error -> error(currentValue.error)
        }
    }
}

object ResourceBoxDefaults {
    val transition = FadeStackTransition()
    val error: @Composable (Throwable) -> Unit = {
        Text(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            text = it.stackTraceToString(),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.body1
        )
    }
    val loading: @Composable () -> Unit = {
        CircularProgressIndicator(modifier = Modifier.center())
    }
}
