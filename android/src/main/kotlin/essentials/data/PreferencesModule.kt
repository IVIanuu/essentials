package essentials.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import essentials.AppScope
import essentials.Scoped
import essentials.coroutines.CoroutineContexts
import essentials.coroutines.ScopedCoroutineScope
import essentials.coroutines.childCoroutineScope
import injekt.Provide

@Provide object PreferencesModule {
  @Provide fun preferencesDataStore(
    coroutineContexts: CoroutineContexts,
    prefsDir: () -> PrefsDir,
    scope: ScopedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<Preferences> = PreferenceDataStoreFactory
    .create(scope = scope.childCoroutineScope(coroutineContexts.io)) {
      prefsDir().resolve("default.preferences_pb")
    }
}
