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

import android.support.design.widget.FloatingActionButton
import android.view.View

var FloatingActionButton.isShow: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        if (value) {
            show()
        } else {
            hide()
        }
    }

var FloatingActionButton.isHide: Boolean
    get() = visibility == View.INVISIBLE || visibility == View.GONE
    set(value) {
        if (value) {
            hide()
        } else {
            show()
        }
    }