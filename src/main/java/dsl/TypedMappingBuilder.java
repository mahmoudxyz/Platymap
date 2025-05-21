package dsl;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TypedMappingBuilder<S, T, V> {
    private final TypedTargetBuilder<S, T> parent;
    private final Function<S, V> sourceAccessor;
    private Function<V, Object> transformation;
    private Predicate<S> condition;

    TypedMappingBuilder(TypedTargetBuilder<S, T> parent, Function<S, V> sourceAccessor) {
        this.parent = parent;
        this.sourceAccessor = sourceAccessor;
    }

    public <U> TypedTargetPathBuilder<S, T, V, U> to(BiConsumer<T, U> targetSetter) {
        return new TypedTargetPathBuilder<>(parent, sourceAccessor, targetSetter, transformation, condition);
    }

    public TypedMappingBuilder<S, T, V> transform(Function<V, Object> transformation) {
        this.transformation = transformation;
        return this;
    }

    public TypedMappingBuilder<S, T, V> when(Predicate<S> condition) {
        this.condition = condition;
        return this;
    }
}
