/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.di

val CoreModule = module {
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.collections.List") null
    else {
      val elementKey = key.arguments[0]
      {
        getFactories(elementKey)
          .map { it(this) }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function0") null
    else {
      val valueKey = key.arguments[0]
      {
        {
          get(valueKey)
        }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function1") null
    else {
      val p1Key = key.arguments[0] as TypeKey<Any?>
      val valueKey = key.arguments[1]
      {
        { p1: Any? ->
          buildChildContainer {
            add(p1Key) { p1 }
          }.get(valueKey)
        }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function2") null
    else {
      val p1Key = key.arguments[0] as TypeKey<Any?>
      val p2Key = key.arguments[1] as TypeKey<Any?>
      val valueKey = key.arguments[2]
      {
        { p1: Any?, p2: Any? ->
          buildChildContainer {
            add(p1Key) { p1 }
            add(p2Key) { p2 }
          }.get(valueKey)
        }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function3") null
    else {
      val p1Key = key.arguments[0] as TypeKey<Any?>
      val p2Key = key.arguments[1] as TypeKey<Any?>
      val p3Key = key.arguments[2] as TypeKey<Any?>
      val valueKey = key.arguments[3]
      {
        { p1: Any?, p2: Any?, p3: Any? ->
          buildChildContainer {
            add(p1Key) { p1 }
            add(p2Key) { p2 }
            add(p3Key) { p3 }
          }.get(valueKey)
        }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function4") null
    else {
      val p1Key = key.arguments[0] as TypeKey<Any?>
      val p2Key = key.arguments[1] as TypeKey<Any?>
      val p3Key = key.arguments[2] as TypeKey<Any?>
      val p4Key = key.arguments[3] as TypeKey<Any?>
      val valueKey = key.arguments[4]
      {
        { p1: Any?, p2: Any?, p3: Any?, p4: Any? ->
          buildChildContainer {
            add(p1Key) { p1 }
            add(p2Key) { p2 }
            add(p3Key) { p3 }
            add(p4Key) { p4 }
          }.get(valueKey)
        }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function5") null
    else {
      val p1Key = key.arguments[0] as TypeKey<Any?>
      val p2Key = key.arguments[1] as TypeKey<Any?>
      val p3Key = key.arguments[2] as TypeKey<Any?>
      val p4Key = key.arguments[3] as TypeKey<Any?>
      val p5Key = key.arguments[4] as TypeKey<Any?>
      val valueKey = key.arguments[5]
      {
        { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any? ->
          buildChildContainer {
            add(p1Key) { p1 }
            add(p2Key) { p2 }
            add(p3Key) { p3 }
            add(p4Key) { p4 }
            add(p5Key) { p5 }
          }.get(valueKey)
        }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function6") null
    else {
      val p1Key = key.arguments[0] as TypeKey<Any?>
      val p2Key = key.arguments[1] as TypeKey<Any?>
      val p3Key = key.arguments[2] as TypeKey<Any?>
      val p4Key = key.arguments[3] as TypeKey<Any?>
      val p5Key = key.arguments[4] as TypeKey<Any?>
      val p6Key = key.arguments[5] as TypeKey<Any?>
      val valueKey = key.arguments[6]
      {
        { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any? ->
          buildChildContainer {
            add(p1Key) { p1 }
            add(p2Key) { p2 }
            add(p3Key) { p3 }
            add(p4Key) { p4 }
            add(p5Key) { p5 }
            add(p6Key) { p6 }
          }.get(valueKey)
        }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function7") null
    else {
      val p1Key = key.arguments[0] as TypeKey<Any?>
      val p2Key = key.arguments[1] as TypeKey<Any?>
      val p3Key = key.arguments[2] as TypeKey<Any?>
      val p4Key = key.arguments[3] as TypeKey<Any?>
      val p5Key = key.arguments[4] as TypeKey<Any?>
      val p6Key = key.arguments[5] as TypeKey<Any?>
      val p7Key = key.arguments[6] as TypeKey<Any?>
      val valueKey = key.arguments[7]
      {
        { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?, p7: Any? ->
          buildChildContainer {
            add(p1Key) { p1 }
            add(p2Key) { p2 }
            add(p3Key) { p3 }
            add(p4Key) { p4 }
            add(p5Key) { p5 }
            add(p6Key) { p6 }
            add(p7Key) { p7 }
          }.get(valueKey)
        }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function8") null
    else {
      val p1Key = key.arguments[0] as TypeKey<Any?>
      val p2Key = key.arguments[1] as TypeKey<Any?>
      val p3Key = key.arguments[2] as TypeKey<Any?>
      val p4Key = key.arguments[3] as TypeKey<Any?>
      val p5Key = key.arguments[4] as TypeKey<Any?>
      val p6Key = key.arguments[5] as TypeKey<Any?>
      val p7Key = key.arguments[6] as TypeKey<Any?>
      val p8Key = key.arguments[7] as TypeKey<Any?>
      val valueKey = key.arguments[8]
      {
        { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?, p7: Any?, p8: Any? ->
          buildChildContainer {
            add(p1Key) { p1 }
            add(p2Key) { p2 }
            add(p3Key) { p3 }
            add(p4Key) { p4 }
            add(p5Key) { p5 }
            add(p6Key) { p6 }
            add(p7Key) { p7 }
            add(p8Key) { p8 }
          }.get(valueKey)
        }
      }
    }
  }
  addProvider { key ->
    return@addProvider if (key.classifierFqName != "kotlin.Function9") null
    else {
      val p1Key = key.arguments[0] as TypeKey<Any?>
      val p2Key = key.arguments[1] as TypeKey<Any?>
      val p3Key = key.arguments[2] as TypeKey<Any?>
      val p4Key = key.arguments[3] as TypeKey<Any?>
      val p5Key = key.arguments[4] as TypeKey<Any?>
      val p6Key = key.arguments[5] as TypeKey<Any?>
      val p7Key = key.arguments[6] as TypeKey<Any?>
      val p8Key = key.arguments[7] as TypeKey<Any?>
      val p9Key = key.arguments[8] as TypeKey<Any?>
      val valueKey = key.arguments[9]
      {
        { p1: Any?, p2: Any?, p3: Any?, p4: Any?, p5: Any?, p6: Any?, p7: Any?, p8: Any?, p9: Any? ->
          buildChildContainer {
            add(p1Key) { p1 }
            add(p2Key) { p2 }
            add(p3Key) { p3 }
            add(p4Key) { p4 }
            add(p5Key) { p5 }
            add(p6Key) { p6 }
            add(p7Key) { p7 }
            add(p7Key) { p8 }
            add(p8Key) { p8 }
            add(p9Key) { p9 }
          }.get(valueKey)
        }
      }
    }
  }
}
