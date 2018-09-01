package com.ivianuu.essentials.ui.simple

import com.airbnb.epoxy.EpoxyController

/**
 * No op epoxy controller
 */
object EmptyEpoxyController : EpoxyController() {
    override fun buildModels() {
    }
}