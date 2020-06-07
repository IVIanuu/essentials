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

package com.ivianuu.essentials.ui.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.compose.Recomposer
import androidx.ui.core.setContent
import com.ivianuu.essentials.ui.core.RetainedObjects
import com.ivianuu.essentials.ui.core.SystemBarManager
import com.ivianuu.essentials.ui.core.UiInitializers
import com.ivianuu.essentials.ui.core.WindowInsetsManager

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity() {

    protected open val containerId: Int
        get() = android.R.id.content

    private val retainedObjects = RetainedObjects()

    private lateinit var composition: Composition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        composition = findViewById<ViewGroup>(containerId).setContent(Recomposer.current()) {
            wrappedContent()
        }
    }

    override fun onDestroy() {
        retainedObjects.dispose()
        composition.dispose()
        super.onDestroy()
    }

    @Composable
    protected open fun wrappedContent() {
        WindowInsetsManager {
            SystemBarManager {
                UiInitializers {
                    content()
                }
            }
        }
    }

    @Composable
    protected abstract fun content()
}
