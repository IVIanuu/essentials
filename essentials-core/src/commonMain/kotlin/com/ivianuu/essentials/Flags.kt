/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

fun Int.setFlag(flag: Int, set: Boolean): Int = if (set) addFlag(flag) else removeFlag(flag)
fun Int.addFlag(flag: Int): Int = this or flag
fun Int.removeFlag(flag: Int): Int = this and flag.inv()
fun Int.hasFlag(flag: Int): Boolean = this and flag == flag
