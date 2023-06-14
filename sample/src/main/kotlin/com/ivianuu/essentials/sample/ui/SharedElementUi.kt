package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.animation.transition.SharedElement
import com.ivianuu.essentials.ui.animation.transition.SharedElementStackTransition
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenConfig
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide val sharedElementsHomeItem = HomeItem("Shared element") { SharedElementScreen(it) }

data class SharedElementScreen(val color: Color) : Screen<Unit> {
  companion object {
    @Provide val config get() = ScreenConfig<SharedElementScreen>(
      transition = SharedElementStackTransition(
        "title Shared element" to "title",
        "color Shared element" to "color"
      )
    )
  }
}

@Provide fun sharedElementKeyUi(navigator: Navigator) = Ui<SharedElementScreen, Unit> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Shared Elements") }) }
  ) {
    Column(
      modifier = Modifier.fillMaxSize()
        .padding(16.dp)
        .scrollable(rememberScrollState(), Orientation.Vertical),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(Modifier.height(16.dp))

      SharedElement(key = "title", isStart = false) {
        Text(
          "Shared element",
          style = MaterialTheme.typography.subtitle1
        )
      }

      Spacer(Modifier.height(16.dp))

      LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(cities) { city ->
          val scope = rememberCoroutineScope()
          Column(
            modifier = Modifier.clickable {
              scope.launch { navigator.push(CityDetailScreen(city)) }
            }.padding(8.dp)
          ) {
            SharedElement(key = "image ${city.name}", isStart = true) {
              Image(
                modifier = Modifier
                  .size(width = 200.dp, height = 100.dp),
                painterResId = city.imageRes,
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
