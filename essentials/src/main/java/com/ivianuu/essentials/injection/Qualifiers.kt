/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.injection

import javax.inject.Qualifier

@Qualifier
annotation class ForApp

@Qualifier
annotation class ForActivity

@Qualifier
annotation class ForReceiver

@Qualifier
annotation class ForController

@Qualifier
annotation class ForChildController

@Qualifier
annotation class ForFragment

@Qualifier
annotation class ForChildFragment

@Qualifier
annotation class ForService

@Qualifier
annotation class ForView

@Qualifier
annotation class ForChildView