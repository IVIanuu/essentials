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

package com.ivianuu.essentials.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.essentials.injection.EssentialsServiceModule
import com.ivianuu.essentials.internal.EssentialsService
import com.ivianuu.essentials.ui.common.ActivityEvent
import com.ivianuu.essentials.util.ext.behaviorSubject
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Handles the lifecycles off all activities
 */
@EssentialsServiceModule
@AutoBindsIntoSet(EssentialsService::class)
class ActivityLifecycleHandler @Inject constructor(
    application: Application
) : EssentialsService, Application.ActivityLifecycleCallbacks {

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val lifecycle = behaviorSubject<ActivityEvent>()
        lifecycles[activity] = lifecycle
        lifecycle.onNext(ActivityEvent.CREATE)
    }

    override fun onActivityStarted(activity: Activity) {
        lifecycles[activity]?.onNext(ActivityEvent.START)
    }

    override fun onActivityResumed(activity: Activity) {
        lifecycles[activity]?.onNext(ActivityEvent.RESUME)
    }

    override fun onActivityPaused(activity: Activity) {
        lifecycles[activity]?.onNext(ActivityEvent.PAUSE)
    }

    override fun onActivityStopped(activity: Activity) {
        lifecycles[activity]?.onNext(ActivityEvent.STOP)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        val lifecycle = lifecycles.remove(activity)
        lifecycle?.onNext(ActivityEvent.DESTROY)
    }

    companion object {

        private val lifecycles =
            mutableMapOf<Activity, BehaviorSubject<ActivityEvent>>()

        fun getLifecycle(activity: Activity): Observable<ActivityEvent> {
            return lifecycles[activity] ?: Observable.never()
        }

        fun peekLifecycle(activity: Activity): ActivityEvent? {
            return lifecycles[activity]?.value
        }

    }
    
}