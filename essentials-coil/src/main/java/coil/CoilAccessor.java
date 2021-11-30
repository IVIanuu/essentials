/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package coil;

import coil.fetch.Fetcher;
import coil.map.Mapper;

@SuppressWarnings("KotlinInternalInJava")
public final class CoilAccessor {

    public static void add(ComponentRegistry.Builder builder, Class type, Fetcher fetcher) {
        builder.add(fetcher, type);
    }

    public static void add(ComponentRegistry.Builder builder, Class type, Mapper mapper) {
        builder.add(mapper, type);
    }

}
