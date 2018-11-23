package com.ivianuu.essentials.ui.traveler.anim

import com.ivianuu.director.Controller
import com.ivianuu.director.RouterTransaction
import com.ivianuu.director.SimpleSwapChangeHandler
import com.ivianuu.director.popChangeHandler
import com.ivianuu.director.pushChangeHandler
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.traveler.Command

/**
 * Key setup for [DialogController]s which keeps the previous view
 */
class DialogControllerKeySetup : ControllerKey.Setup {
    override fun apply(
        command: Command,
        currentController: Controller?,
        nextController: Controller,
        transaction: RouterTransaction
    ) {
        transaction
            .pushChangeHandler(SimpleSwapChangeHandler(removesFromViewOnPush = false))
            .popChangeHandler(SimpleSwapChangeHandler(removesFromViewOnPush = false))
    }
}