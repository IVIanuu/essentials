package androidx.compose;

import org.jetbrains.annotations.NotNull;

public final class ComposableLayoutCompositionReference implements CompositionReference {

    private final CompositionReference delegate;

    private Composer composer;

    public ComposableLayoutCompositionReference(CompositionReference delegate) {
        this.delegate = delegate;
    }

    @NotNull
    @Override
    public BuildableMap<Ambient<Object>, ValueHolder<Object>> getAmbientScope() {
        return delegate.getAmbientScope();
    }

    @Override
    public <T> T getAmbient(@NotNull Ambient<T> ambient) {
        return delegate.getAmbient(ambient);
    }

    @Override
    public <N> void registerComposer(@NotNull Composer<N> composer) {
        this.composer = composer;
    }

    @Override
    public void invalidate() {
        if (composer != null) //noinspection KotlinInternalInJava
            Recomposer.Companion.current().scheduleRecompose$compose_runtime_release(composer);
    }
}
