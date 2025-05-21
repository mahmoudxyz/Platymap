package xyz.mahmoudahmed.dsl;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TypedForEachBuilder<S, T, V> {
    private final TypedTargetBuilder<S, T> parent;
    private final Function<S, List<V>> collectionAccessor;

    TypedForEachBuilder(TypedTargetBuilder<S, T> parent, Function<S, List<V>> collectionAccessor) {
        this.parent = parent;
        this.collectionAccessor = collectionAccessor;
    }

    public <C> TypedForEachItemBuilder<S, T, V, C> createIn(
            BiConsumer<T, List<C>> targetCollectionSetter) {
        return new TypedForEachItemBuilder<>(parent, collectionAccessor, targetCollectionSetter);
    }
}