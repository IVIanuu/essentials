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

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.layout.*
import kotlin.time.*

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
                contentPadding = localVerticalInsetsPadding()
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
            LazyRow(contentPadding = localHorizontalInsetsPadding()) {
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
    val transition = FadeStackTransition(defaultAnimationSpec(150.milliseconds, easing = FastOutSlowInEasing))
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
