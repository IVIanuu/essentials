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

package com.ivianuu.essentials.util

import eu.chainfire.libsuperuser.Shell
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shell
 */
@Singleton
class Shell @Inject constructor() {

    fun run(command: String) = run(listOf(command))

    fun run(commands: Collection<String>) =
        run(commands.toTypedArray())

    fun run(commands: Array<String>): Single<List<String>> =
        Single.fromCallable<List<String>> { Shell.SU.run(commands) }

    fun available(): Single<Boolean> = Single.fromCallable<Boolean> { Shell.SU.available() }

}