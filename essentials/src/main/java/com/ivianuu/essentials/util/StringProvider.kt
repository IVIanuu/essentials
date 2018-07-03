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

import android.app.Application
import javax.inject.Inject

/**
 * Provides [String]'s from resources
 */
class StringProvider @Inject constructor(private val app: Application) {

    fun getString(resId: Int): String = app.getString(resId)

    fun getString(resId: Int, vararg args: Any): String = app.getString(resId, *args)

}