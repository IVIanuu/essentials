package com.ivianuu.essentials.systemoverlay.runner

import androidx.compose.runtime.Composable

abstract class SystemOverlayId(val value: String) {
    override fun equals(other: Any?): Boolean = other is SystemOverlayId && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "SystemOverlay[$value]"
}

sealed class SystemOverlayAttachState<T : SystemOverlayId> {
    data class Attached<T : SystemOverlayId>(
        val config: SystemOverlayConfig
    ) : SystemOverlayAttachState<T>()
    object Detached : SystemOverlayAttachState<Nothing>()
}

typealias SystemOverlayUi<T> = @Composable () -> Unit

data class SystemOverlayConfig(
    val triggers: List<TriggerConfig>
)

data class TriggerConfig(
    val position: TriggerPosition,
    val size: Int,
    val sensitivity: Int,
    val location: Float,
    val rotate: Boolean
)
