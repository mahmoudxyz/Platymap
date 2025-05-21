package xyz.mahmoudahmed.dsl;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TypedNestedMappingBuilder<S, T, V, C, P> {
    private final TypedForEachItemBuilder<S, T, V, C> parent;
    private final Function<V, P> sourceAccessor;
    private Function<P, Object> transformation;

    TypedNestedMappingBuilder(TypedForEachItemBuilder<S, T, V, C> parent, Function<V, P> sourceAccessor) {
        this.parent = parent;
        this.sourceAccessor = sourceAccessor;
    }

    public <R> TypedNestedTargetBuilder<S, T, V, C, P, R> to(BiConsumer<C, R> targetSetter) {
        return new TypedNestedTargetBuilder<>(parent, sourceAccessor, targetSetter, transformation);
    }

    public TypedNestedMappingBuilder<S, T, V, C, P> transform(Function<P, Object> transformation) {
        this.transformation = transformation;
        return this;
    }
}