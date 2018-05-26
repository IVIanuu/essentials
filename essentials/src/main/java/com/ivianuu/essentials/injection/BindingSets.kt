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
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ivianuu.daggerextensions.BindingSet
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.base.BaseFragment

@BindingSet([Application::class])
annotation class AppBindingSet

@BindingSet([Activity::class, FragmentActivity::class, AppCompatActivity::class, BaseActivity::class])
annotation class ActivityBindingSet

@BindingSet([BroadcastReceiver::class])
annotation class BroadcastReceiverBindingSet

@BindingSet([ContentProvider::class])
annotation class ContentProviderBindingSet

@BindingSet([Fragment::class, BaseFragment::class])
annotation class FragmentBindingSet

@BindingSet([DialogFragment::class, Fragment::class])
annotation class DialogFragmentBindingSet

@BindingSet([Service::class])
annotation class ServiceBindingSet

@BindingSet([View::class])
annotation class ViewBindingSet