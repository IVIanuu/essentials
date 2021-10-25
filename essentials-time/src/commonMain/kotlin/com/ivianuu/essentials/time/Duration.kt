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

package com.ivianuu.essentials.time

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

inline val Int.nanoseconds get() = toDuration(DurationUnit.NANOSECONDS)

inline val Long.nanoseconds get() = toDuration(DurationUnit.NANOSECONDS)

inline val Double.nanoseconds get() = toDuration(DurationUnit.NANOSECONDS)

inline val Int.microseconds get() = toDuration(DurationUnit.MICROSECONDS)

inline val Long.microseconds get() = toDuration(DurationUnit.MICROSECONDS)

inline val Double.microseconds get() = toDuration(DurationUnit.MICROSECONDS)

inline val Int.milliseconds get() = toDuration(DurationUnit.MILLISECONDS)

inline val Long.milliseconds get() = toDuration(DurationUnit.MILLISECONDS)

inline val Double.milliseconds get() = toDuration(DurationUnit.MILLISECONDS)

inline val Int.seconds get() = toDuration(DurationUnit.SECONDS)

inline val Long.seconds get() = toDuration(DurationUnit.SECONDS)

inline val Double.seconds get() = toDuration(DurationUnit.SECONDS)

inline val Int.minutes get() = toDuration(DurationUnit.MINUTES)

inline val Long.minutes get() = toDuration(DurationUnit.MINUTES)

inline val Double.minutes get() = toDuration(DurationUnit.MINUTES)

inline val Int.hours get() = toDuration(DurationUnit.HOURS)

inline val Long.hours get() = toDuration(DurationUnit.HOURS)

inline val Double.hours get() = toDuration(DurationUnit.HOURS)

inline val Int.days get() = toDuration(DurationUnit.DAYS)

inline val Long.days get() = toDuration(DurationUnit.DAYS)

inline val Double.days get() = toDuration(DurationUnit.DAYS)

expect fun Long.toDuration(): Duration

expect fun Duration.toLong(): Long
