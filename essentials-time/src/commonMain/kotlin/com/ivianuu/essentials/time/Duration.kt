/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
