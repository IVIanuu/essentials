/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.job

fun CoroutineScope.childCoroutineScope(
  context: CoroutineContext = EmptyCoroutineContext,
): CoroutineScope = CoroutineScope(context + childJob())

suspend fun childCoroutineScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope =
  CoroutineScope(context + childJob())

fun CoroutineScope.childJob() = coroutineContext.job.childJob()

suspend fun childJob() = coroutineContext.job.childJob()

fun Job.childJob() = Job(parent = this)
