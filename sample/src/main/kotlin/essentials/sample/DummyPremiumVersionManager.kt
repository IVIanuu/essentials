package essentials.sample

import essentials.premium.IsPremiumVersion
import injekt.*

@Provide val isPremiumVersion: IsPremiumVersion get() = true
