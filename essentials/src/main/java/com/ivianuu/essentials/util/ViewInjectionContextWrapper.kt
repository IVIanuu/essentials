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

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import com.ivianuu.daggerextensions.view.HasViewInjector
import dagger.android.AndroidInjector

/**
 * Wraps a [Context] and is a [HasViewInjector]
 * to make it possible to inject stuff from a [android.support.v4.app.Fragment]
 * into [View]'s
 */
class ViewInjectionContextWrapper(
    context: Context,
    private val viewInjector: HasViewInjector
) : ContextWrapper(context), HasViewInjector {

    override fun viewInjector(): AndroidInjector<View> = viewInjector.viewInjector()

}