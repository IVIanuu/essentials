package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.activityresult.registerActivityResultListener
import com.ivianuu.director.activityresult.startActivityForResult
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.injekt.inject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivityResultKey(
    val resultCode: Int,
    val intent: Intent,
    val requestCode: Int
) : ControllerKey(::ActivityResultController, NavOptions().dialog())

/**
 * Activity result controller
 */
class ActivityResultController : EsController() {

    private val key by inject<ActivityResultKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerActivityResultListener(key.requestCode) { requestCode, resultCode, data ->
            travelerRouter.goBackWithResult(
                key.resultCode,
                ActivityResult(requestCode, resultCode, data)
            )
        }

        startActivityForResult(key.intent, key.requestCode)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ) = View(activity)

}

data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
)

val ActivityResult.isOk: Boolean get() = resultCode == Activity.RESULT_OK
val ActivityResult.isCanceled: Boolean get() = resultCode == Activity.RESULT_CANCELED
val ActivityResult.isFirstUser: Boolean get() = resultCode == Activity.RESULT_FIRST_USER