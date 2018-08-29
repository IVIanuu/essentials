package com.ivianuu.essentials.ui.common

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.ivianuu.compass.Destination
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination

@Destination(PermissionActivity::class)
data class PermissionDestination(
    override val resultCode: Int,
    val permissions: Array<String>,
    val requestCode: Int = RequestCodeGenerator.generate()
) : ResultDestination

/**
 * Activity result activity
 */
class PermissionActivity : BaseActivity() {

    private val destination by bindPermissionDestination()

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(destination.permissions, destination.requestCode)
        } else {
            router.sendResult(destination.resultCode, PermissionResult(
                destination.requestCode,
                destination.permissions,
                destination.permissions
                    .map { PackageManager.PERMISSION_GRANTED }
                    .toIntArray()
            ))

            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        router.sendResult(
            destination.resultCode,
            PermissionResult(requestCode, permissions, grantResults)
        )
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}

data class PermissionResult(
    val requestCode: Int,
    val permissions: Array<out String>,
    val grantResults: IntArray
) {

    val allGranted get() = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

}