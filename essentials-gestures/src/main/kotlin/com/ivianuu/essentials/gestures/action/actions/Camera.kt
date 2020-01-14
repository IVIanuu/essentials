package com.ivianuu.essentials.gestures.action.actions

/**
import android.content.Intent
import android.provider.MediaStore
import com.ivianuu.pie.R
import com.ivianuu.pie.action.Action
import com.ivianuu.pie.data.Flag

private fun createCameraAction() = Action(
    key = KEY_CAMERA,
    title = string(R.string.action_camera),
    states = stateless(R.drawable.ic_photo_camera),
    flags = setOf(Flag.UnlockScreen)
)

private fun openCamera() {
    startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
}*/