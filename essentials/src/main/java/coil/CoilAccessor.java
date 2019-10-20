package coil;

import coil.fetch.Fetcher;
import coil.map.Mapper;
import coil.map.MeasuredMapper;

@SuppressWarnings("KotlinInternalInJava")
public final class CoilAccessor {

    public static void add(ComponentRegistry.Builder builder, Class type, Fetcher fetcher) {
        builder.add(type, fetcher);
    }

    public static void add(ComponentRegistry.Builder builder, Class type, Mapper mapper) {
        builder.add(type, mapper);
    }

    public static void add(ComponentRegistry.Builder builder, Class type, MeasuredMapper mapper) {
        builder.add(type, mapper);
    }

}
