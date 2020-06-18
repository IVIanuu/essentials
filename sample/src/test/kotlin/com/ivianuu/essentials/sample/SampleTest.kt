package com.ivianuu.essentials.sample

import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class SampleTest {

    @Test
    fun test() {
        expectThat(0)
            .isA<Int>()
            .isEqualTo(0)
    }

}
