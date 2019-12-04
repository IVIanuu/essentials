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

package com.ivianuu.essentials.hidenavbar

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.IBinder
import android.view.Display
import com.github.ajalt.timberkt.d
import com.ivianuu.injekt.Factory

/**
 * Utils to access overscan with reflection
 */
@Factory
@SuppressLint("PrivateApi")
internal class OverscanHelper {

    fun setOverscan(rect: Rect) {
        d { "set overscan $rect" }
        setOverscanMethod.invoke(
            windowManagerService,
            Display.DEFAULT_DISPLAY, rect.left, rect.top, rect.right, rect.bottom
        )
    }

    private companion object {
        private val windowManagerService by lazy {
            val cls = Class.forName("android.view.IWindowManager\$Stub")
            val invoke = Class.forName("android.os.ServiceManager")
                .getMethod("checkService", String::class.java)
                .invoke(null, "window")

            cls.getMethod("asInterface", IBinder::class.java)
                .invoke(null, invoke)
        }

        private val setOverscanMethod by lazy {
            windowManagerService.javaClass.getDeclaredMethod(
                "setOverscan",
                Int::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java
            ).apply { isAccessible = true }
        }
    }
}
