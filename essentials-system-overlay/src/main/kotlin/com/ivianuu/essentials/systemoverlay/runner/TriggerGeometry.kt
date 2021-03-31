package com.ivianuu.essentials.systemoverlay.runner

import com.ivianuu.essentials.screenstate.DisplayInfo
import com.ivianuu.essentials.screenstate.DisplayRotation
import com.ivianuu.injekt.Given

data class TriggerGeometry(
    val position: TriggerPosition,
    val width: Int,
    val height: Int,
    val horizontalMargin: Int,
    val verticalMargin: Int,
)

typealias TriggerGeometryFactory = (TriggerConfig, DisplayInfo) -> TriggerGeometry

@Given
fun triggerGeometryFactory(): TriggerGeometryFactory = { triggerConfig, displayInfo ->
    val position = triggerConfig.position.rotate(
        if (triggerConfig.rotate) DisplayRotation.PORTRAIT_UP
        else displayInfo.rotation
    )

    val width: Int
    val height: Int
    val horizontalMargin: Int
    val verticalMargin: Int
    if (position.isOnSide) {
        width = triggerConfig.sensitivity
        height = displayInfo.screenHeight * triggerConfig.size
        horizontalMargin = 0
        verticalMargin = ((displayInfo.screenHeight - height) * triggerConfig.location).toInt()
    } else {
        width = displayInfo.screenWidth * triggerConfig.size
        height = triggerConfig.sensitivity
        horizontalMargin = (
                (displayInfo.screenWidth - width) *
                        if (displayInfo.rotation == DisplayRotation.LANDSCAPE_RIGHT) 1f - triggerConfig.location
                        else triggerConfig.location
                ).toInt()
        verticalMargin = 0
    }

    TriggerGeometry(
        width = width,
        height = height,
        horizontalMargin = horizontalMargin,
        verticalMargin = verticalMargin,
        position = position
    )
}
