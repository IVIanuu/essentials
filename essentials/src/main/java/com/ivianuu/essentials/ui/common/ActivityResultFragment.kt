package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination
import com.ivianuu.essentials.ui.traveler.key.BaseFragmentDestination
import com.ivianuu.essentials.ui.traveler.key.bindDestination
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivityResultDestination(
    override val resultCode: Int,
    val intent: Intent,
    val requestCode: Int = RequestCodeGenerator.generate()
) : BaseFragmentDestination(ActivityResultFragment::class), ResultDestination<ActivityResult>

/**
 * Activity result fragment
 */
class ActivityResultFragment : BaseFragment() {

    private val destination by bindDestination<ActivityResultDestination>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityForResult(destination.intent, destination.requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        router.goBackWithResult(
            destination.resultCode,
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