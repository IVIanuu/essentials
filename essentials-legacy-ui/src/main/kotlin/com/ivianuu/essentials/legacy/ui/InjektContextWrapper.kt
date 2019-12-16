/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.legacy.ui

import android.content.Context
import android.content.ContextWrapper
import com.ivianuu.injekt.InjektTrait

/**
 * Wraps a [Context] and is a [InjektTrait]
 * to make it possible to inject stuff from a [com.ivianuu.director.Controller]
 * into [android.view.View]s or other [Context] layers
 */
class InjektTraitContextWrapper(
    context: Context,
    InjektTrait: InjektTrait
) : ContextWrapper(context), InjektTrait by InjektTrait
