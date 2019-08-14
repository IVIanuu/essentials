package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ivianuu.compose.ActivityAmbient
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.common.navigator
import com.ivianuu.compose.onActive

// todo Route + Navigator is the wrong api for this case

fun ActivityResultRoute(intent: Intent) = Route(isFloating = true) {
    val activity = ambient(ActivityAmbient) as FragmentActivity
    val navigator = navigator
    onActive {
        ActivityResultFragment.get(activity).startForResult(intent) {
            navigator.pop(it)
        }
    }
}

data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
)

val ActivityResult.isOk: Boolean get() = resultCode == Activity.RESULT_OK
val ActivityResult.isCanceled: Boolean get() = resultCode == Activity.RESULT_CANCELED
val ActivityResult.isFirstUser: Boolean get() = resultCode == Activity.RESULT_FIRST_USER

class ActivityResultFragment : Fragment() {

    private val callbacksByResultCode = mutableMapOf<Int, (ActivityResult) -> Unit>()

    init {
        retainInstance = true
    }

    fun startForResult(
        intent: Intent,
        callback: (ActivityResult) -> Unit
    ): () -> Unit {
        val requestCode = nextResultCode()
        callbacksByResultCode[requestCode] = callback
        startActivityForResult(intent, requestCode)
        return { callbacksByResultCode.remove(requestCode) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbacksByResultCode.remove(requestCode)
            ?.invoke(ActivityResult(requestCode, resultCode, data))
    }

    internal companion object {
        private val TAG = ActivityResultFragment::class.java.canonicalName
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