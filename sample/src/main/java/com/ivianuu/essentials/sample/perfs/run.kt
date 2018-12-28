package com.ivianuu.essentials.sample.perfs

import com.ivianuu.injekt.component
import com.ivianuu.injekt.get
import com.ivianuu.injekt.measureDuration
import com.ivianuu.injekt.measureDurationOnly
import com.ivianuu.timberktx.d

fun runPerfTest() {
    // runEmptyTest()
    run400Test()
}

private fun runEmptyTest() {
    for (i in 0 until 1) {
        val (_, startDuration) = measureDuration { component() }
        d { "test $i empty started in $startDuration ms" }
    }
}

private fun run400Test() {
    var startMax = -1.0
    var startMin = -1.0
    val startAll = arrayListOf<Double>()

    for (i in 0 until 100) {
        val (component, startDuration) = measureDuration { component(perfModule400()) }

        if (i != 0) {
            if (startMax == -1.0 || startDuration > startMax) {
                startMax = startDuration
            }

            if (startMin == -1.0 || startDuration < startMin) {
                startMin = startDuration
            }

            startAll.add(startDuration)
        }

        d { "test $i 400 started in $startDuration ms" }

        val executionDuration = measureDurationOnly {
            component.get<Perfs.A27>()
            component.get<Perfs.B31>()
            component.get<Perfs.C12>()
            component.get<Perfs.D42>()
        }

        d { "test $i 400 executed in $executionDuration ms" }
    }

    val avgStartTime = startAll.fold(0.0) { d, d1 -> d + d1 } / startAll.size

    d { "completed 400 test average: $avgStartTime max: $startMax min $startMin" }
}