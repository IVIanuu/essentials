/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

inline fun <reified P1, reified T> Container.resolve(func: (P1) -> T) = func(get())

inline fun <reified P1, reified P2, reified T> Container.resolve(func: (P1, P2) -> T) =
  func(get(), get())

inline fun <reified P1, reified P2, reified P3, reified T> Container.resolve(func: (P1, P2, P3) -> T) =
  func(get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified T> Container.resolve(func: (P1, P2, P3, P4) -> T) =
  func(get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified T> Container.resolve(func: (P1, P2, P3, P4, P5) -> T) =
  func(get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6) -> T) =
  func(get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7) -> T) =
  func(get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

inline fun <reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified P21, reified T> Container.resolve(func: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> T) =
  func(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
