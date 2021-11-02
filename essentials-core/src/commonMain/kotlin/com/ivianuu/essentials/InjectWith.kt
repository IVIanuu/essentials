/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.Inject

inline fun <P1, T> injectWith(p1: P1, @Inject factory: (P1) -> T): T = factory(p1)

inline fun <P1, P2, T> injectWith(
  p1: P1,
  p2: P2,
  @Inject factory: (P1, P2) -> T
): T = factory(p1, p2)

inline fun <P1, P2, P3, T> injectWith(
  p1: P1,
  p2: P2,
  p3: P3,
  @Inject factory: (P1, P2, P3) -> T
): T = factory(p1, p2, p3)

inline fun <P1, P2, P3, P4, T> injectWith(
  p1: P1,
  p2: P2,
  p3: P3,
  p4: P4,
  @Inject factory: (P1, P2, P3, P4) -> T
): T = factory(p1, p2, p3, p4)

inline fun <P1, P2, P3, P4, P5, T> injectWith(
  p1: P1,
  p2: P2,
  p3: P3,
  p4: P4,
  p5: P5,
  @Inject factory: (P1, P2, P3, P4, P5) -> T
): T = factory(p1, p2, p3, p4, p5)

inline fun <P1, P2, P3, P4, P5, P6, T> injectWith(
  p1: P1,
  p2: P2,
  p3: P3,
  p4: P4,
  p5: P5,
  p6: P6,
  @Inject factory: (P1, P2, P3, P4, P5, P6) -> T
): T = factory(p1, p2, p3, p4, p5, p6)

inline fun <P1, P2, P3, P4, P5, P6, P7, T> injectWith(
  p1: P1,
  p2: P2,
  p3: P3,
  p4: P4,
  p5: P5,
  p6: P6,
  p7: P7,
  @Inject factory: (P1, P2, P3, P4, P5, P6, P7) -> T
): T = factory(p1, p2, p3, p4, p5, p6, p7)

inline fun <P1, P2, P3, P4, P5, P6, P7, P8, T> injectWith(
  p1: P1,
  p2: P2,
  p3: P3,
  p4: P4,
  p5: P5,
  p6: P6,
  p7: P7,
  p8: P8,
  @Inject factory: (P1, P2, P3, P4, P5, P6, P7, P8) -> T
): T = factory(p1, p2, p3, p4, p5, p6, p7, p8)


inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, T> injectWith(
  p1: P1,
  p2: P2,
  p3: P3,
  p4: P4,
  p5: P5,
  p6: P6,
  p7: P7,
  p8: P8,
  p9: P9,
  @Inject factory: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> T
): T = factory(p1, p2, p3, p4, p5, p6, p7, p8, p9)
