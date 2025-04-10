package essentials.data

import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*

@Provide object PreferenceProviders {
  @Provide fun dataStore(
    coroutineContexts: CoroutineContexts,
    dirs: () -> AppDirs,
    scope: @For<AppScope> CoroutineScope
  ): @Scoped<AppScope> DataStore<Preferences> = PreferenceDataStoreFactory
    .create(scope = scope + coroutineContexts.io) {
      dirs().data.resolve("default.preferences_pb")
    }
}
