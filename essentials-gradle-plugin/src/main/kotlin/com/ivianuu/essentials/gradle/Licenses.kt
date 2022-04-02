/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gradle

import com.jaredsburrows.license.LicensePlugin
import com.jaredsburrows.license.LicenseReportExtension

fun EssentialsExtension.withLicenses() {
  project.plugins.apply(LicensePlugin::class.java)
  project.extensions.getByType(LicenseReportExtension::class.java).run {
    generateCsvReport = false
    generateHtmlReport = false
    generateJsonReport = true
    copyJsonReportToAssets = true
  }
}
