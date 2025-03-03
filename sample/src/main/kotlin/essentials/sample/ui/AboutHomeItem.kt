/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import essentials.about.*
import essentials.donation.*
import injekt.*

@Provide val aboutHomeItem = HomeItem("About") {
  AboutScreen(
    donationScreen = DonationScreen(),
    privacyPolicyUrl = "https://www.google.com"
  )
}
