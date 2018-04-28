/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.util.ext

import timber.log.Timber

inline fun v(t: Throwable? = null, message: () -> String) = log { Timber.v(t, message()) }
inline fun v(t: Throwable?) = Timber.v(t)

inline fun d(t: Throwable? = null, message: () -> String) = log { Timber.d(t, message()) }
inline fun d(t: Throwable?) = Timber.d(t)

inline fun i(t: Throwable? = null, message: () -> String) = log { Timber.i(t, message()) }
inline fun i(t: Throwable?) = Timber.i(t)

inline fun w(t: Throwable? = null, message: () -> String) = log { Timber.w(t, message()) }
inline fun w(t: Throwable?) = Timber.w(t)

inline fun e(t: Throwable? = null, message: () -> String) = log { Timber.e(t, message()) }
inline fun e(t: Throwable?) = Timber.e(t)

inline fun wtf(t: Throwable? = null, message: () -> String) = log { Timber.wtf(t, message()) }
inline fun wtf(t: Throwable?) = Timber.wtf(t)

@PublishedApi
internal inline fun log(block: () -> Unit) {
    if (Timber.treeCount() > 0) block()
}