package com.ivianuu.essentials.compiler

import org.junit.Test

class PropertyTypesTest {
  @Test fun testValidGetterSubTypeIsOk() = codegen(
    """
      class MyVm {
        val list = mutableListOf<Int>()
          @com.ivianuu.essentials.PublicType<List<Int>> get
      }
    """
  )

  @Test fun testInvalidGetterSubTypeIsNotOk() = codegen(
    """
      class MyVm {
        val list = mutableListOf<Int>()
          @com.ivianuu.essentials.PublicType<Set<Int>> get
      }
    """
  ) {
    compilationShouldHaveFailed("property annotation type is not sub type of property type")
  }

  @Test fun testCanUsePrivateTypeFromInside() = codegen(
    """
      class MyVm {
        val list = mutableListOf<Int>()
          @com.ivianuu.essentials.PublicType<List<Int>> get

        fun add(item: Int) = list.add(item)
      }
    """
  )

  @Test fun testCanUsePrivateTypeFromInside2() = codegen(
    """
      class MyVm {
        val list = mutableListOf<Int>()
        val anyInt = 0
          @com.ivianuu.essentials.PublicType<Any> get
        fun invoke() {
          list.add(anyInt)
        }
      }
    """
  )

  @Test fun testCanUsePrivateTypeFromInside3() = codegen(
    """
      class MyVm {
        val state = kotlinx.coroutines.flow.MutableStateFlow(0)
          @com.ivianuu.essentials.PublicType<kotlinx.coroutines.flow.StateFlow<Int>> get
        fun invoke() {
          state.value = 1
        }
      }
    """
  )

  @Test fun testCannotUsePrivateTypeFromOutside() = codegen(
    """
      class MyVm {
        val list = mutableListOf<Int>()
          @com.ivianuu.essentials.PublicType<List<Int>> get
      }
      
      fun invoke() {
        val myVm = MyVm()
        val list: MutableList<Int> = myVm.list
      }
    """
  ) {
    compilationShouldHaveFailed("property annotation type is not sub type of property type")
  }

  @Test fun testCannotUsePrivateTypeFromOutside2() = codegen(
    """
      class MyVm {
        val list = mutableListOf<Int>()
        val anyInt = 0
          @com.ivianuu.essentials.PublicType<Any> get
      }
      
      fun invoke() {
        val myVm = MyVm()
        myVm.list.add(myVm.anyInt)
      }
    """
  ) {
    compilationShouldHaveFailed("property annotation type is not sub type of property type")
  }

  @Test fun testCannotUsePrivateTypeFromOutside3() = codegen(
    """
      class MyVm {
        val state = kotlinx.coroutines.flow.MutableStateFlow(0)
          @com.ivianuu.essentials.PublicType<kotlinx.coroutines.flow.StateFlow<Int>> get
      }
      
      fun invoke() {
        val myVm = MyVm()
        myVm.state.value = 1
      }
    """
  ) {
    compilationShouldHaveFailed("property annotation type is not sub type of property type")
  }
}
