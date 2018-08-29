package com.ivianuu.essentials.ui.common

import android.content.Intent
import android.os.Bundle
import com.ivianuu.compass.Destination
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination

@Destination(ActivityResultActivity::class)
data class ActivityResultDestination(
    override val resultCode: Int,
    val intent: Intent,
    val requestCode: Int = RequestCodeGenerator.generate()
) : ResultDestination

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
        router.sendResult(destination.resultCode, ActivityResult(requestCode, resultCode, data))
        overridePendingTransition(0, 0)
        finish()
    }

}

data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
)