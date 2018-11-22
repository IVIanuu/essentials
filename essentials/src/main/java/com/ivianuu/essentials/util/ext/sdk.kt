package com.ivianuu.essentials.util.ext

import android.os.Build

fun isSdk(version: Int) = Build.VERSION.SDK_INT >= version

fun isLMR1() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
fun isM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isN() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isNMR1() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
fun isO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun isOMR1() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
fun isP() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P