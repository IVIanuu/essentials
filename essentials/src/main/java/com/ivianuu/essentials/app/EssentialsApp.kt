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

package com.ivianuu.essentials.app

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.support.v4.app.Fragment
import dagger.android.*
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * App
 */
abstract class EssentialsApp : Application(), HasActivityInjector, HasBroadcastReceiverInjector, HasContentProviderInjector, HasServiceInjector, HasSupportFragmentInjector {

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    @Inject lateinit var contentProviderInjector: DispatchingAndroidInjector<ContentProvider>
    @Inject lateinit var serviceInjector: DispatchingAndroidInjector<Service>
    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = broadcastReceiverInjector

    override fun contentProviderInjector(): AndroidInjector<ContentProvider> = contentProviderInjector

    override fun serviceInjector(): AndroidInjector<Service> = serviceInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector
}