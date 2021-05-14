package com.ivianuu.essentials.gradle

import com.jaredsburrows.license.*

fun EssentialsExtension.withLicenses() {
  project.plugins.apply(LicensePlugin::class.java)
  project.extensions.getByType(LicenseReportExtension::class.java).run {
    generateCsvReport = false
    generateHtmlReport = false
    generateJsonReport = true
    copyJsonReportToAssets = true
  }
}
