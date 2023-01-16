/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.db

import androidx.test.core.app.ApplicationProvider
import com.ivianuu.essentials.test.runCancellingBlockingTest
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class) class RelationTest {
  @Test fun testSimpleRelation() = runCancellingBlockingTest {
    val db = AndroidDb(
      context = ApplicationProvider.getApplicationContext(),
      name = "mydb.db",
      schema = Schema(
        version = 1,
        entities = listOf(UserEntity, PlaylistEntity)
      ),
      coroutineContext = coroutineContext
    )

    db.insert(UserEntity("user", PlaylistEntity("playlist")))

    db.selectById<UserEntity>("user").first()
  }

  @Entity @Serializable data class UserEntity(
    @PrimaryKey val id: String,
    @Relation val playlist: PlaylistEntity
  ) {
    companion object : AbstractEntityDescriptor<UserEntity>("users")
  }

  @Entity @Serializable data class PlaylistEntity(
    @PrimaryKey val id: String
  ) {
    companion object : AbstractEntityDescriptor<PlaylistEntity>("playlists")
  }
}
