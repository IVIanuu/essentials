package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Given
val sharedElementsHomeItem = HomeItem("Shared element") { SharedElementKey(it) }

data class SharedElementKey(val color: Color) : Key<Nothing>

@Given
fun sharedElementKeyUi(
    @Given key: SharedElementKey,
    @Given navigator: Navigator
): KeyUi<SharedElementKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Shared Elements") }) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
                .scrollable(rememberScrollState(), Orientation.Vertical),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SharedCircleBadge("color", key.color, 150.dp, false)

            Spacer(Modifier.height(16.dp))

            SharedElement(key = "title", isStart = false) {
                Text(
                    "Shared element",
                    style = MaterialTheme.typography.subtitle1
                )
            }

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                items(cities) { city ->
                    Column(
                        modifier = Modifier.clickable { navigator.push(CityDetailKey(city)) }
                            .padding(8.dp)
                    ) {
                        SharedElement(key = "image ${city.name}", isStart = true) {
                            Image(
                                modifier = Modifier
                                    .size(width = 200.dp, height = 100.dp),
                                painter = painterResource(city.imageRes),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        SharedElement(key = "title ${city.name}", isStart = true) {
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }
            }
        }
    }
}

data class City(val name: String, val imageRes: Int)

val cities = listOf(
    City("Chicago", R.drawable.chicago),
    City("Jakarta", R.drawable.jakarta),
    City("London", R.drawable.london),
    City("Sao Paulo", R.drawable.sao_paulo),
    City("Tokyo", R.drawable.tokyo)
)

@Given
val sharedElementsNavigationOptionFactory: KeyUiOptionsFactory<SharedElementKey> = {
    KeyUiOptions(
        transition = SharedElementStackTransition(
            "title Shared element" to "title",
            "color Shared element" to "color"
        )
    )
}
