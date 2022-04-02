/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.about.AboutKey
import com.ivianuu.essentials.about.PrivacyPolicyUrl
import com.ivianuu.injekt.Provide

@Provide val aboutHomeItem = HomeItem("About") { AboutKey }

@Provide val privacyPolicy = PrivacyPolicyUrl("https://www.google.com")
