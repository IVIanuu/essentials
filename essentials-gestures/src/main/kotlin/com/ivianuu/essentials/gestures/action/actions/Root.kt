package com.ivianuu.essentials.gestures.action.actions

import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param

@Factory
class RootActionExecutor(
    @Param private val command: String,
    private val shell: Shell
) : ActionExecutor {
    override suspend fun invoke() {
        try {
            shell.run(command)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}