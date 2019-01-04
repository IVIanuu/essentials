package com.ivianuu.essentials.ui.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.startActivityForResult
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.key
import com.ivianuu.essentials.util.ext.goBackWithResult
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

    private val key by key<ActivityResultKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityForResult(key.intent, key.requestCode)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ) = View(activity)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        travelerRouter.goBackWithResult(
            key.resultCode,
            ActivityResult(requestCode, resultCode, data)
        )
    }

}

data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
)

val ActivityResult.isOk get() = resultCode == Activity.RESULT_OK
val ActivityResult.isCanceled get() = resultCode == Activity.RESULT_CANCELED
val ActivityResult.isFirstUser get() = resultCode == Activity.RESULT_FIRST_USER