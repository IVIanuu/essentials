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

package com.ivianuu.essentials.injection

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.support.v4.app.Fragment
import android.view.View
import com.ivianuu.daggerextensions.view.HasViewInjector
import dagger.android.*
import dagger.android.support.HasSupportFragmentInjector

interface KtHasActivityInjector : HasActivityInjector {
    val activityInjector: AndroidInjector<Activity>
    override fun activityInjector() = activityInjector
}

interface KtHasBroadcastReceiverInjector : HasBroadcastReceiverInjector {
    val broadcastReceiverInjector: AndroidInjector<BroadcastReceiver>
    override fun broadcastReceiverInjector() = broadcastReceiverInjector
}

interface KtHasContentProviderInjector : HasContentProviderInjector {
    val contentProviderInjector: AndroidInjector<ContentProvider>
    override fun contentProviderInjector() = contentProviderInjector
}

interface KtHasServiceInjector : HasServiceInjector {
    val serviceInjector: AndroidInjector<Service>
    override fun serviceInjector() = serviceInjector
}

interface KtHasSupportFragmentInjector : HasSupportFragmentInjector {
    val supportFragmentInjector: AndroidInjector<Fragment>
    override fun supportFragmentInjector() = supportFragmentInjector
}

interface KtHasViewInjector : HasViewInjector {
    val viewInjector: AndroidInjector<View>
    override fun viewInjector() = viewInjector
}