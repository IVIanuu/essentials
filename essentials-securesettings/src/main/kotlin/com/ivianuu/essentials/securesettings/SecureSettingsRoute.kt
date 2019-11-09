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
import com.ivianuu.essentials.ui.compose.common.ScrollableList
import com.ivianuu.essentials.ui.compose.common.navigateOnClick
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.coroutines.coroutineScope
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.copy
import com.ivianuu.essentials.ui.navigation.director.defaultControllerRouteOptionsOrElse
import com.ivianuu.essentials.ui.navigation.director.defaultControllerRouteOptionsOrNull
import com.ivianuu.essentials.ui.navigation.director.horizontal
import com.ivianuu.essentials.util.Toaster
import kotlinx.coroutines.launch

/**
 * Asks the user for the secure settings permission
 */
fun secureSettingsRoute(showHideNavBarHint: Boolean = false) =
    composeControllerRoute(
        options = defaultControllerRouteOptionsOrNull()
    ) {
        +popNavigatorOnceSecureSettingsGranted()

        Scaffold(
            topAppBar = { EsTopAppBar(+stringResource(R.string.es_title_secure_settings)) },
            body = {
                ScrollableList {
                    Padding(padding = 16.dp) {
                        val textColor = (+textColorForBackground(
                            +ambient(
                                CurrentBackground
                            )
                        ))!!.copy(alpha = 0.6f)

                        Text(
                            text = +stringResource(
                                if (showHideNavBarHint) {
                                    R.string.es_pref_secure_settings_header_hide_nav_bar_summary
                                } else {
                                    R.string.es_pref_secure_settings_header_summary
                                }
                            ),
                            style = (+themeTextStyle { body2 }).copy(color = textColor)
                        )
                    }

                    SimpleListItem(
                        title = { Text(+stringResource(R.string.es_pref_use_pc)) },
                        subtitle = { Text(+stringResource(R.string.es_pref_use_pc_summary)) },
                        onClick = +navigateOnClick {
                            secureSettingsInstructionsRoute.copy(
                                options = defaultControllerRouteOptionsOrElse {
                                    controllerRouteOptions().horizontal()
                                }
                            )
                        }
                    )

                    val coroutineScope = +coroutineScope()
                    val secureSettingsHelper = +inject<SecureSettingsHelper>()
                    val toaster = +inject<Toaster>()
                    SimpleListItem(
                        title = { Text(+stringResource(R.string.es_pref_use_root)) },
                        subtitle = { Text(+stringResource(R.string.es_pref_use_root_summary)) },
                        onClick = {
                            coroutineScope.launch {
                                if (secureSettingsHelper.grantWriteSecureSettingsViaRoot()) {
                                    toaster.toast(R.string.es_secure_settings_permission_granted)
                                } else {
                                    toaster.toast(R.string.es_secure_settings_no_root)
                                }
                            }
                        }
                    )
                }
            }
        )
    }