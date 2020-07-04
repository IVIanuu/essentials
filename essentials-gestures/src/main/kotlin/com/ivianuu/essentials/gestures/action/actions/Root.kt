package com.ivianuu.essentials.gestures.action.actions

import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Unscoped

@Unscoped
class RootActionExecutor(
    @Assisted private val command: String,
    private val shell: Shell,
    private val toaster: Toaster
) : ActionExecutor {
    override suspend fun invoke() {
        try {
            shell.run(command)
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.toast(R.string.es_no_root)
        }
    }
}
