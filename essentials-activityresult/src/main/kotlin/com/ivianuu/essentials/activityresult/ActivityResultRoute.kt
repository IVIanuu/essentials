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
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.retained
import com.ivianuu.essentials.ui.compose.coroutines.retainedCoroutineScope
import com.ivianuu.essentials.ui.compose.es.ActivityAmbient
import com.ivianuu.essentials.ui.compose.navigation.Route
import com.ivianuu.essentials.ui.compose.navigation.navigator
import com.ivianuu.essentials.util.cast
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

fun ActivityResultRoute(intent: Intent) = Route(
    opaque = true
) {
    val activity = ambient(ActivityAmbient)

    val retainedScope = retainedCoroutineScope("${System.identityHashCode(intent)}:scope")
    val activityResultFragment = ActivityResultFragment.get(activity.cast())
    val navigator = navigator
    retained("${System.identityHashCode(intent)}:launch") {
        retainedScope.launch {
            val result = activityResultFragment.startForResult(intent)
            navigator.pop(result = result)
        }
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

class ActivityResultFragment : Fragment() {

    private val resultsByRequestCode = mutableMapOf<Int, CompletableDeferred<ActivityResult>>()

    init {
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        d { "on result $requestCode $resultCode $data" }
        resultsByRequestCode.remove(requestCode)
            ?.complete(ActivityResult(requestCode, resultCode, data))
    }

    internal suspend fun startForResult(intent: Intent): ActivityResult {
        val requestCode = requestCodes.incrementAndGet()

        val result = CompletableDeferred<ActivityResult>()
        resultsByRequestCode[requestCode] = result

        d { "start for result $requestCode $intent" }

        try {
            startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            resultsByRequestCode.remove(requestCode)
                ?.completeExceptionally(e)
        }

        return result.await()
    }

    companion object {
        private const val TAG = "com.ivianuu.essentials.activityresult.ActivityResultFragment"
        private val requestCodes = AtomicInteger(0)
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
