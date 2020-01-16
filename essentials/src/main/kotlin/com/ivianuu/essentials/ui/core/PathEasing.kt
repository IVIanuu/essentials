package com.ivianuu.essentials.ui.core

import androidx.animation.Easing
import androidx.ui.graphics.Path

class PathEasing(path: Path) : Easing {

    private val x: FloatArray
    private val y: FloatArray

    init {
        val pointComponents = path.toFrameworkPath().approximate(PRECISION)

        val numPoints = pointComponents.size / 3

        x = FloatArray(numPoints)
        y = FloatArray(numPoints)
        var prevX = 0f
        var prevFraction = 0f
        var componentIndex = 0
        for (i in 0 until numPoints) {
            val fraction = pointComponents[componentIndex++]
            val x = pointComponents[componentIndex++]
            val y = pointComponents[componentIndex++]
            require(!(fraction == prevFraction && x != prevX)) { "The Path cannot have discontinuity in the X axis." }
            require(x >= prevX) { "The Path cannot loop back on itself." }
            this.x[i] = x
            this.y[i] = y
            prevX = x
            prevFraction = fraction
        }
    }

    override fun invoke(t: Float): Float {
        if (t <= 0) return 0f
        if (t >= 1) return 1f

        var startIndex = 0
        var endIndex = x.lastIndex

        while (endIndex - startIndex > 1) {
            val midIndex = (startIndex + endIndex) / 2
            if (t < x[midIndex]) {
                endIndex = midIndex
            } else {
                startIndex = midIndex
            }
        }

        val xRange = x[endIndex] - x[startIndex]
        if (xRange == 0f) return y[startIndex]

        val fractionInRange = t - x[startIndex]
        val fraction = fractionInRange / xRange

        val startY = y[startIndex]
        val endY = y[endIndex]
        return startY + fraction * (endY - startY)
    }

    private companion object {
        private const val PRECISION = 0.002f
    }
}