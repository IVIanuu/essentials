package essentials.data

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import essentials.AppScope
import essentials.Scoped
import essentials.coroutines.CoroutineContexts
import essentials.coroutines.ScopedCoroutineScope
import essentials.coroutines.childCoroutineScope
import injekt.Provide
import kotlinx.coroutines.flow.Flow

@Provide object PreferencesModule {
  @Provide fun preferencesDataStore(
    coroutineContexts: CoroutineContexts,
    prefsDir: () -> PrefsDir,
    scope: ScopedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<Preferences> {
    val androidXDataStore by lazy {
      PreferenceDataStoreFactory
        .create(scope = scope.childCoroutineScope(coroutineContexts.io)) {
          prefsDir().resolve("default.preferences_pb")
        }
    }
    return object : DataStore<Preferences> {
      override val data: Flow<Preferences>
        get() = androidXDataStore.data

      override suspend fun updateData(transform: Preferences.() -> Preferences): Preferences =
        androidXDataStore.updateData(transform)
    }
  }
}

suspend fun DataStore<Preferences>.edit(
  transform: MutablePreferences.() -> Unit
): Preferences = updateData {
  toMutablePreferences().apply { transform(this) }
}
