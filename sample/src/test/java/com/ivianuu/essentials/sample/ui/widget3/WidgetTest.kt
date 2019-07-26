package com.ivianuu.essentials.sample.ui.widget3

import com.ivianuu.essentials.sample.ui.widget3.core.canUpdate
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class WidgetTest {

    @Test
    fun testCanUpdate() {
        // not the same id
        var childOne = TestWidget(null)
        childOne.id = "id"
        var childTwo = TestWidget(null)
        childTwo.id = "other_id"
        assertFalse(childOne.canUpdate(childTwo))

        // not the same key
        childOne = TestWidget("key")
        childOne.id = "id"
        childTwo = TestWidget("other_key")
        childTwo.id = "id"
        assertFalse(childOne.canUpdate(childTwo))

        // same id and no key
        childOne = TestWidget(null)
        childOne.id = "id"
        childTwo = TestWidget(null)
        childTwo.id = "id"
        assertTrue(childOne.canUpdate(childTwo))

        // same id and key
        childOne = TestWidget("key")
        childOne.id = "id"
        childTwo = TestWidget("key")
        childTwo.id = "id"
        assertTrue(childOne.canUpdate(childTwo))
    }

}