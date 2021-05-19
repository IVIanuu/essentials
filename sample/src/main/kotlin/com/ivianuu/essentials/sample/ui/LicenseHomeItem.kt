package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.license.ui.*
import com.ivianuu.injekt.*

@Provide val licenseHomeItem = HomeItem("License") { LicenseKey }
