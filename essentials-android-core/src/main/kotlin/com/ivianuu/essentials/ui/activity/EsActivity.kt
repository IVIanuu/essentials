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

package com.ivianuu.essentials.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.compose.Providers
import androidx.compose.Recomposer
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.ui.core.setContent
import androidx.ui.savedinstancestate.UiSavedStateRegistry
import androidx.ui.savedinstancestate.UiSavedStateRegistryAmbient
import com.ivianuu.essentials.ui.core.ProvideInsets
import com.ivianuu.essentials.ui.core.ProvideSystemBarManager

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity() {

    protected open val containerId: Int
        get() = android.R.id.content

    private lateinit var composition: Composition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = findViewById<ViewGroup>(containerId)
        ViewTreeLifecycleOwner.set(container, this)
        ViewTreeViewModelStoreOwner.set(container, this)

        composition = container.setContent(Recomposer.current()) {
            wrappedContent()
        }
    }

    override fun onDestroy() {
        composition.dispose()
        super.onDestroy()
    }

    @Composable
    protected open fun wrappedContent() {
        ProvideInsets {
            ProvideSystemBarManager {
                val uiSavedStateRegistry = UiSavedStateRegistry(
                    restoredValues = emptyMap(),
                    canBeSaved = { true }
                )
                Providers(
                    UiSavedStateRegistryAmbient provides uiSavedStateRegistry
                ) {
                    content()
                }
            }
        }
    }

    @Composable
    protected abstract fun content()
}
