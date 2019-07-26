package com.ivianuu.essentials.sample.ui.widget3

import junit.framework.Assert.*
import org.junit.Test

class ElementTest {

    @Test
    fun testMount() {
        val owner = TestBuildOwner()
        val element = TestWidget().createElement()
        element.mount(owner, null)

        assertEquals(owner, element.owner)
        assertNull(element.parent)
    }

    @Test
    fun testUnmount() {
        val element = TestWidget { +TestWidget() }.createElement()
        element.mount(TestBuildOwner(), null)
        element.unmount()

        assertNull(element.owner)
        assertNull(element.parent)
        assertNull(element.children)
    }

    @Test
    fun testChildMount() {
        val owner = TestBuildOwner()

        // todo test emit behavior separately

        val parent = TestWidget { +TestWidget() }.createElement()
        parent.mount(owner, null)

        val child = parent.children!!.first()

        assertEquals(child.owner, parent.owner)
        assertEquals(child.parent, parent)
    }

    @Test
    fun testChildUnmount() {
        val owner = TestBuildOwner()

        val parent = TestWidget { +TestWidget() }.createElement()
        parent.mount(owner, null)

        val child = parent.children!!.first()

        parent.unmount()

        assertNull(child.owner)
        assertNull(child.parent)
        assertNull(child.children)
    }

    @Test
    fun testUpdate() {
        val widgetOne = TestWidget()
        val element = widgetOne.createElement()
        element.mount(TestBuildOwner(), null)
        assertEquals(widgetOne, element.widget)

        val widgetTwo = TestWidget()
        element.update(widgetTwo)
        assertEquals(widgetTwo, element.widget)
    }

    @Test
    fun testChildAdd() {
        val parent = TestWidget().createElement()
        parent.mount(TestBuildOwner(), null)

        assertNull(parent.children)

        val child = TestWidget()

        val newWidget = TestWidget { +child }

        parent.update(newWidget)

        assertNotNull(parent.children)
        assertEquals(1, parent.children!!.size)
        assertEquals(child, parent.children!!.first().widget)
    }

    @Test
    fun testChildRemove() {
        val child = TestWidget()

        val parent = TestWidget { +child }.createElement()
        parent.mount(TestBuildOwner(), null)

        assertNotNull(parent.children)
        assertEquals(1, parent.children!!.size)
        assertEquals(child, parent.children!!.first().widget)

        val newWidget = TestWidget()

        parent.update(newWidget)

        assertNull(parent.children)
    }

    @Test
    fun testChildUpdate() {
        val oldChildWidget = TestWidget()

        val parent = TestWidget { emit("id", oldChildWidget) }.createElement()
        parent.mount(TestBuildOwner(), null)

        assertNotNull(parent.children)
        assertEquals(1, parent.children!!.size)
        val oldChildElement = parent.children!!.first()
        assertEquals(oldChildWidget, oldChildElement.widget)

        val newChildWidget = TestWidget()
        parent.update(TestWidget {
            emit("id", newChildWidget)
        })

        assertNotNull(parent.children)
        assertEquals(1, parent.children!!.size)
        val newChildElement = parent.children!!.first()
        assertEquals(oldChildElement, newChildElement)
        assertEquals(newChildWidget, newChildElement.widget)
    }

    @Test
    fun testChildMove() {
        val widgetA = TestWidget()
        val widgetB = TestWidget()

        val parent = TestWidget {
            emit("a", widgetA)
            emit("b", widgetB)
        }.createElement()
        parent.mount(TestBuildOwner(), null)

        assertEquals(widgetA, parent.children!![0].widget)
        assertEquals(widgetB, parent.children!![1].widget)

        val elementA = parent.children!![0]
        val elementB = parent.children!![1]

        parent.update(TestWidget {
            emit("b", widgetB)
            emit("a", widgetA)
        })

        assertEquals(1, parent.children!!.indexOf(elementA))
        assertEquals(0, parent.children!!.indexOf(elementB))
    }

}