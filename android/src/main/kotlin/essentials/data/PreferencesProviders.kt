package essentials.data

import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import essentials.*
import essentials.coroutines.*
import injekt.*

@Provide object PreferencesProviders {
  @Provide fun preferencesDataStore(
    coroutineContexts: CoroutineContexts,
    prefsDir: () -> PrefsDir,
    scope: ScopedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<Preferences> = PreferenceDataStoreFactory
    .create(scope = scope.childCoroutineScope(coroutineContexts.io)) {
      prefsDir().resolve("default.preferences_pb")
    }
}
