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

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Component

@Component interface Context1<A> {
  @Provide val a: A
}

@Component interface Context2<A, B> : Context1<A> {
  @Provide val b: B
}

@Component interface Context3<A, B, C> : Context2<A, B> {
  @Provide val c: C
}

@Component interface Context4<A, B, C, D> : Context3<A, B, C> {
  @Provide val d: D
}

@Component interface Context5<A, B, C, D, E> : Context4<A, B, C, D> {
  @Provide val e: E
}

@Component interface Context6<A, B, C, D, E, F> : Context5<A, B, C, D, E> {
  @Provide val f: F
}

@Component interface Context7<A, B, C, D, E, F, G> : Context6<A, B, C, D, E, F> {
  @Provide val g: G
}

@Component interface Context8<A, B, C, D, E, F, G, H> : Context7<A, B, C, D, E, F, G> {
  @Provide val h: H
}

@Component interface Context9<A, B, C, D, E, F, G, H, I> : Context8<A, B, C, D, E, F, G, H> {
  @Provide val i: I
}
