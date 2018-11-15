package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.key.BaseFragmentKey
import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.essentials.ui.traveler.key.bindKey
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivityResultKey(
    override val resultCode: Int,
    val intent: Intent,
    val requestCode: Int = RequestCodeGenerator.generate()
) : BaseFragmentKey(ActivityResultFragment::class),
    ResultKey<ActivityResult>

/**
 * Activity result fragment
 */
class ActivityResultFragment : BaseFragment() {

    private val key by bindKey<ActivityResultKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityForResult(key.intent, key.requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        router.goBackWithResult(
            key.resultCode,
            ActivityResult(requestCode, resultCode, data)
        )
    }

}

data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
) {
    val isOk get() = resultCode == Activity.RESULT_OK
    val isCanceled get() = resultCode == Activity.RESULT_CANCELED
    val isFirstUser get() = resultCode == Activity.RESULT_FIRST_USER
}