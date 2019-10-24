package com.ivianuu.essentials.sample.ui

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Draw
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.OnPositioned
import androidx.ui.core.Opacity
import androidx.ui.core.PxSize
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.engine.geometry.Offset
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.about.aboutRoute
import com.ivianuu.essentials.apps.ui.appPickerRoute
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.twilight.twilightSettingsRoute
import com.ivianuu.essentials.ui.compose.common.ListScreen
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsAppBarIcon
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.ScaffoldAmbient
import com.ivianuu.essentials.ui.compose.material.showPopup
import com.ivianuu.essentials.ui.compose.resources.drawableResource
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Inject

data class CoordinatesHolder(var coordinates: LayoutCoordinates?)

val homeRoute = composeControllerRoute {
    ListScreen(
        appBar = {
            EsTopAppBar(
                title = { Text("Home") },
                trailing = {
                    val scaffold = +ambient(ScaffoldAmbient)
                    val toaster = +inject<Toaster>()

                    Container {
                        val coordinates = +memo { CoordinatesHolder(null) }
                        OnPositioned { coordinates.coordinates = it }

                        EsAppBarIcon(
                            image = +drawableResource(R.drawable.abc_ic_menu_overflow_material),
                            onClick = {
                                val localToGlobal = coordinates.coordinates!!
                                    .localToGlobal(scaffold.coordinates!!.position)
                                coordinates.coordinates!!.size
                                var rootCoordinates = coordinates.coordinates!!
                                while (rootCoordinates.parentCoordinates != null) {
                                    rootCoordinates = rootCoordinates.parentCoordinates!!
                                }

                                d { "local to global ${localToGlobal.x.value}, ${localToGlobal.y.value} size ${coordinates.coordinates!!.size.height}" }

                                scaffold.showPopup(
                                    alignment = Alignment.TopRight,
                                    offsetX = 4.dp,
                                    offsetY = 4.dp,
                                    items = listOf(
                                        "Option 1",
                                        "Option 2",
                                        "Option 3"
                                    ),
                                    item = { Text(text = it) },
                                    onSelected = { toaster.toast("Clicked $it") }
                                )
                            }
                        )
                    }
                }
            )
        },
        listContent = {
            val navigator = +inject<Navigator>()
            HomeItem.values().forEachIndexed { index, item ->
                HomeItem(item = item, onClick = {
                    navigator.push(item.route())
                })

                if (index != HomeItem.values().lastIndex) {
                    HomeDivider()
                }
            }
        }
    )
}

@Composable
private fun HomeItem(
    item: HomeItem,
    onClick: () -> Unit
) = composable(item) {
    ListItem(
        text = { Text(item.title) },
        icon = { ColorAvatar(item.color) },
        onClick = onClick
    )
}

@Composable
private fun ColorAvatar(color: Color) = composable("ColorAvatar") {
    Container(width = 40.dp, height = 40.dp) {
        val paint = +memo {
            Paint().apply { this.color = color }
        }
        Draw { canvas: Canvas, parentSize: PxSize ->
            canvas.drawCircle(
                Offset(parentSize.width.value / 2, parentSize.height.value / 2),
                parentSize.width.value / 2,
                paint
            )
        }
    }
}

@Composable
private fun HomeDivider() = composable("HomeDivider") {
    Padding(left = 72.dp) {
        Opacity(0.12f) {
            Divider()
        }
    }
}

enum class HomeItem(
    val title: String,
    val color: Color,
    val route: () -> Route
) {
    Counter(
        title = "Counter",
        color = Color.Red,
        route = { counterRoute }
    ),
    AppPicker(
        title = "App Picker",
        color = Color.Blue,
        route = { appPickerRoute(true) }
    ),
    CheckApps(
        title = "Check Apps",
        color = Color.Green,
        route = { checkAppsRoute }
    ),
    Twilight(
        title = "Twilight",
        color = Color.Gray,
        route = { twilightSettingsRoute }
    ),
    Compose(
        title = "Compose",
        color = Color.Magenta,
        route = { composeRoute }
    ),
    Timer(
        title = "Timer",
        color = Color.Cyan,
        route = { com.ivianuu.essentials.sample.ui.timerRoute }
    ),
    About(
        title = "About",
        color = Color.Yellow,
        route = { aboutRoute() }
    )
}

@Inject
class DisplayInfo(
    private val context: Context,
    private val windowManager: WindowManager
) {
    val isLandscape: Boolean
        get() = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidth: Int
        get() {
            windowManager.defaultDisplay.getRealMetrics(metrics)
            return metrics.widthPixels
        }
    val screenHeight: Int
        get() {
            windowManager.defaultDisplay.getRealMetrics(metrics)
            return metrics.heightPixels
        }
    /*val screenRotation: Rotation
        get() = Rotation.values().first { it.rotationInt == windowManager.defaultDisplay.rotation }*/

    private companion object {
        private val metrics = DisplayMetrics()
    }
}