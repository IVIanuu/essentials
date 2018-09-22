package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ivianuu.compass.Destination
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.traveler.goBackWithResult

@Destination(ActivityResultActivity::class)
data class ActivityResultDestination(
    override val resultCode: Int,
    val intent: Intent,
    val requestCode: Int = RequestCodeGenerator.generate()
) : ResultDestination<ActivityResult>

/**
 * Activity result activity
 */
class ActivityResultActivity : BaseActivity() {

    private val destination by bindActivityResultDestination()

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        startActivityForResult(destination.intent, destination.requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        router.goBackWithResult(
            destination.resultCode,
            ActivityResult(requestCode, resultCode, data)
        )
        overridePendingTransition(0, 0)
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