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

package com.ivianuu.essentials.sample.ui.checkapps

import com.ivianuu.essentials.apps.ui.CheckableAppsFragment
import com.ivianuu.essentials.ui.traveler.anim.NavOptions
import com.ivianuu.essentials.ui.traveler.anim.fade
import com.ivianuu.essentials.ui.traveler.key.FragmentKey
import com.ivianuu.injekt.get
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.rx.asObservable
import com.ivianuu.kprefs.stringSet
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize

@Parcelize
object CheckAppsKey : FragmentKey(::CheckAppsFragment, options = NavOptions().fade())

/**
 * @author Manuel Wrage (IVIanuu)
 */
class CheckAppsFragment : CheckableAppsFragment() {

    override val toolbarTitle: String?
        get() = "Send check apps"

    private val pref by lazy {
        get<KPrefs>().stringSet("apps")
    }

    override fun getCheckedAppsObservable(): Observable<Set<String>> =
        pref.asObservable()

    override fun onCheckedAppsChanged(apps: Set<String>) {
        pref.set(apps)
    }

}