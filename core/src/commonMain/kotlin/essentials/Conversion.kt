package essentials

import androidx.compose.runtime.*
import essentials.compose.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide fun <T> (@Composable () -> T).asFlow(): Flow<T> =
  moleculeFlow { this() }
