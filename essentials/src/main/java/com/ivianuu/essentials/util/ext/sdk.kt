package com.ivianuu.essentials.util.ext

import android.os.Build

fun isSdk(version: Int): Boolean = Build.VERSION.SDK_INT >= version

fun isLMR1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
fun isM(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isN(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isNMR1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
fun isO(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun isOMR1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
fun isP(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P