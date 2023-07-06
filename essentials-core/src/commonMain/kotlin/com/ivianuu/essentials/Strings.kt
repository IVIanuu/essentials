package com.ivianuu.essentials

import androidx.compose.runtime.Stable
import com.ivianuu.injekt.Provide

@Stable interface Strings {
  operator fun get(key: Key0): String

  operator fun <P1> get(key: Key1<P1>, p1: P1): String

  operator fun <P1, P2> get(key: Key2<P1, P2>, p1: P1, p2: P2): String

  operator fun <P1, P2, P3> get(key: Key3<P1, P2, P3>, p1: P1, p2: P2, p3: P3): String

  operator fun <P1, P2, P3, P4> get(key: Key4<P1, P2, P3, P4>, p1: P1, p2: P2, p3: P3, p4: P4): String

  operator fun <P1, P2, P3, P4, P5> get(key: Key5<P1, P2, P3, P4, P5>, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5): String

  @Stable class Key0(internal val default: () -> String)

  @Stable class Key1<P1>(internal val default: (P1) -> String)

  @Stable class Key2<P1, P2>(internal val default: (P1, P2) -> String)

  @Stable class Key3<P1, P2, P3>(internal val default: (P1, P2, P3) -> String)

  @Stable class Key4<P1, P2, P3, P4>(internal val default: (P1, P2, P3, P4) -> String)

  @Stable class Key5<P1, P2, P3, P4, P5>(internal val default: (P1, P2, P3, P4, P5) -> String)
}

@Provide class StringsImpl : Strings {
  override fun get(key: Strings.Key0) = key.default()
  override fun <P1> get(key: Strings.Key1<P1>, p1: P1) = key.default(p1)
  override fun <P1, P2> get(key: Strings.Key2<P1, P2>, p1: P1, p2: P2) = key.default(p1, p2)
  override fun <P1, P2, P3> get(key: Strings.Key3<P1, P2, P3>, p1: P1, p2: P2, p3: P3) =
    key.default(p1, p2, p3)

  override fun <P1, P2, P3, P4> get(
    key: Strings.Key4<P1, P2, P3, P4>,
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4
  ) = key.default(p1, p2, p3, p4)

  override fun <P1, P2, P3, P4, P5> get(
    key: Strings.Key5<P1, P2, P3, P4, P5>,
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5
  ) = key.default(p1, p2, p3, p4, p5)
}

fun stringKeyOf(default: () -> String): Strings.Key0 = Strings.Key0(default)

fun <P1> stringKeyOf(default: (P1) -> String) = Strings.Key1(default)

fun <P1, P2> stringKeyOf(default: (P1, P2) -> String) = Strings.Key2(default)

fun <P1, P2, P3> stringKeyOf(default: (P1, P2, P3) -> String) = Strings.Key3(default)

val Strings_Cancel = stringKeyOf { "Cancel" }
val Strings_Close = stringKeyOf { "Close" }
val Strings_Ok = stringKeyOf { "OK" }
val Strings_Yes = stringKeyOf { "Yes" }
val Strings_No = stringKeyOf { "No" }
