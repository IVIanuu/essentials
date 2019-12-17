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

package com.ivianuu.essentials.legacy.ui.simple

import androidx.coordinatorlayout.widget.CoordinatorLayout
import kotlinx.android.synthetic.main.es_controller_list.es_coordinator_layout

/**
 * A controller which hosts a coordinator layout
 */
abstract class CoordinatorController : MvRxController() {

    open val coordinatorLayout: CoordinatorLayout
        get() = es_coordinator_layout
}
