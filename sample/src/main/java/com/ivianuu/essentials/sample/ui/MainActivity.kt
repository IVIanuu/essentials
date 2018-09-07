
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

package com.ivianuu.essentials.sample.ui

import android.os.Bundle
import com.ivianuu.essentials.data.app.AppInfo
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.common.AppPickerDestination
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.navigateToForResult
import dagger.Binds
import dagger.Module

class MainActivity : BaseActivity() {

    override val startDestination: Any?
        get() = CounterDestination(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launchUi {
            val appInfo =
                router.navigateToForResult<AppInfo>(AppPickerDestination("Hello", 1))
            d { "on result -> $appInfo" }
        }
    }

}

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun bindBaseActivity(mainActivity: MainActivity): BaseActivity

}