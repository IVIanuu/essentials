@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import com.f2prateek.rx.preferences2.Preference
import com.ivianuu.epoxyprefs.PreferenceModel
import com.ivianuu.epoxyprefs.urlClickListener

inline fun PreferenceModel.Builder.applyFromRxPreference(preference: Preference<*>) {
    key(preference.key())
    defaultValue(preference.defaultValue())
}

inline fun PreferenceModel.Builder.googlePlusCommunityClickListener(id: String) {
    urlClickListener(googlePlusCommunityUrl(id))
}

inline fun PreferenceModel.Builder.playStoreDetailsClickListener(packageName: String) {
    urlClickListener(playStoreDetailsUrl(packageName))
}