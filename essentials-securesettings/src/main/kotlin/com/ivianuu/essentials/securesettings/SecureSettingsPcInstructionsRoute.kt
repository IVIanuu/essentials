/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.securesettings

import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Padding
import androidx.ui.material.surface.CurrentBackground
import androidx.ui.material.textColorForBackground
import androidx.ui.material.themeTextStyle
import androidx.ui.res.stringResource
import com.ivianuu.essentials.ui.compose.common.openUrlOnClick
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollableList
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Icon
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.resources.drawableResource
import com.ivianuu.essentials.ui.navigation.director.defaultControllerRouteOptionsOrNull
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.StringProvider
import com.ivianuu.essentials.util.Toaster

/**
 * Asks the user for the secure settings permission
 */
val secureSettingsInstructionsRoute =
    composeControllerRoute(
        options = defaultControllerRouteOptionsOrNull()
    ) {
        +popNavigatorOnceSecureSettingsGranted()

        Scaffold(
            topAppBar = { EsTopAppBar(title = +stringResource(R.string.es_title_secure_settings_pc_instructions)) },
            body = {
                ScrollableList {
                    Padding(padding = 16.dp) {
                        val textColor = (+textColorForBackground(
                            +ambient(
                                CurrentBackground
                            )
                        ))!!.copy(alpha = 0.6f)
                        Text(
                            text = +stringResource(R.string.es_pref_secure_settings_pc_instructions_header_summary),
                            style = (+themeTextStyle { body2 }).copy(color = textColor)
                        )
                    }

                    SimpleListItem(
                        title = { Text(+stringResource(R.string.es_pref_secure_settings_step_1)) },
                        subtitle = { Text(+stringResource(R.string.es_pref_secure_settings_step_1_summary)) }
                    )

                    SimpleListItem(
                        title = { Text(+stringResource(R.string.es_pref_secure_settings_step_2)) },
                        subtitle = { Text(+stringResource(R.string.es_pref_secure_settings_step_2_summary)) }
                    )

                    SimpleListItem(
                        title = { Text(+stringResource(R.string.es_pref_secure_settings_step_3)) },
                        subtitle = { Text(+stringResource(R.string.es_pref_secure_settings_step_3_summary)) }
                    )

                    SimpleListItem(
                        leading = { Icon(+drawableResource(R.drawable.es_ic_link)) },
                        title = { Text(+stringResource(R.string.es_pref_secure_settings_link_gadget_hacks_summary)) },
                        onClick = +openUrlOnClick { "https://youtu.be/CDuxcrrWLnY" }
                    )

                    SimpleListItem(
                        leading = { Icon(+drawableResource(R.drawable.es_ic_link)) },
                        title = { Text(+stringResource(R.string.es_pref_secure_settings_link_lifehacker_summary)) },
                        onClick = +openUrlOnClick {
                            "https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378"
                        }
                    )

                    SimpleListItem(
                        leading = { Icon(+drawableResource(R.drawable.es_ic_link)) },
                        title = { Text(+stringResource(R.string.es_pref_secure_settings_link_xda_summary)) },
                        onClick = +openUrlOnClick {
                            "https://www.xda-developers.com/install-adb-windows-macos-linux/"
                        }
                    )

                    val buildInfo = +inject<BuildInfo>()
                    val clipboardAccessor = +inject<ClipboardAccessor>()
                    val stringProvider = +inject<StringProvider>()
                    val toaster = +inject<Toaster>()

                    SimpleListItem(
                        title = { Text(+stringResource(R.string.es_pref_secure_settings_step_4)) },
                        subtitle = {
                            Text(
                                stringProvider.getString(
                                    R.string.es_pref_secure_settings_step_4_summary,
                                    buildInfo.packageName
                                )
                            )
                        },
                        onClick = {
                            clipboardAccessor.clipboardText =
                                "adb shell pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS"

                            toaster.toast(R.string.es_copied_to_clipboard)
                        }
                    )
                }
            }
        )
    }