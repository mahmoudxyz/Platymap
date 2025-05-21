package xyz.mahmoudahmed.dsl;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TypedBranchMappingBuilder<S, T, V> {
    private final TypedBranchActionBuilder<S, T> parent;
    private final Function<S, V> sourceAccessor;
    private Function<V, Object> transformation;

    TypedBranchMappingBuilder(TypedBranchActionBuilder<S, T> parent, Function<S, V> sourceAccessor) {
        this.parent = parent;
        this.sourceAccessor = sourceAccessor;
    }

    public <U> TypedBranchTargetBuilder<S, T, V, U> to(BiConsumer<T, U> targetSetter) {
        return new TypedBranchTargetBuilder<>(parent, sourceAccessor, targetSetter, transformation);
    }

    public TypedBranchMappingBuilder<S, T, V> transform(Function<V, Object> transformation) {
        this.transformation = transformation;
        return this;
    }
}