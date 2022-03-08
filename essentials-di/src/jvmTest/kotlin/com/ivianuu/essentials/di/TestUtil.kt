/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

class Foo

class Bar(val foo: Foo)

object TestScope

interface Command

class CommandA : Command

class CommandB : Command
