/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.license.ui.LicenseKey
import com.ivianuu.injekt.Provide

@Provide val licenseHomeItem = HomeItem("License") { LicenseKey }
