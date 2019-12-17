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

package com.ivianuu.essentials.activityresult

import android.app.Activity
import android.content.Intent
import androidx.compose.ambient
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.ui.compose.common.retained
import com.ivianuu.essentials.ui.compose.es.ActivityAmbient
import com.ivianuu.essentials.ui.compose.es.ComposeActivity
import com.ivianuu.essentials.ui.compose.navigation.NavigatorState
import com.ivianuu.essentials.ui.compose.navigation.Route
import com.ivianuu.essentials.util.cast
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

fun ActivityResultRoute(intent: Intent) = Route(
    opaque = true
) {
    val activity = ambient(ActivityAmbient)
    retained {
        ActivityResultFragment.get(activity.cast())
            .startForResult(intent)
    }
}

data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
) {
    val isOk: Boolean get() = resultCode == Activity.RESULT_OK
    val isCanceled: Boolean get() = resultCode == Activity.RESULT_CANCELED
    val isFirstUser: Boolean get() = resultCode == Activity.RESULT_FIRST_USER
}

@Factory
internal class ActivityResultFragment() : Fragment() {

    private val navigator: NavigatorState by unsafeLazy {
        requireActivity().cast<ComposeActivity>()
            .component.get()
    }

    init {
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        navigator.pop(
            ActivityResult(
                requestCode,
                resultCode,
                data
            )
        )
    }

    internal fun startForResult(intent: Intent) {
        val requestCode = requestCodes.incrementAndGet()

        try {
            startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            lifecycleScope.launch { navigator.pop() }
        }
    }

    companion object {
        private const val TAG = "com.ivianuu.essentials.activityresult.ActivityResultFragment"
        fun get(activity: FragmentActivity): ActivityResultFragment {
            var fragment =
                activity.supportFragmentManager.findFragmentByTag(TAG) as? ActivityResultFragment
            if (fragment == null) {
                fragment = ActivityResultFragment()
                activity.supportFragmentManager.beginTransaction()
                    .add(fragment, TAG)
                    .commitNow()
            }
            return fragment
        }
    }
}

private val requestCodes = AtomicInteger(0)
