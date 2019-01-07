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

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

val coroutinesDefault: CoroutineDispatcher get() = Dispatchers.Default
val coroutinesIo: CoroutineDispatcher get() = Dispatchers.IO
val coroutinesMain: MainCoroutineDispatcher get() = Dispatchers.Main

val rxComputation: Scheduler get() = Schedulers.computation()
val rxIo: Scheduler get() = Schedulers.io()
val rxMain: Scheduler get() = AndroidSchedulers.mainThread()