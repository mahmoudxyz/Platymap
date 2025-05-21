package xyz.mahmoudahmed.dsl;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TypedNestedTargetBuilder<S, T, V, C, P, R> {
    private final TypedForEachItemBuilder<S, T, V, C> parent;
    private final Function<V, P> sourceAccessor;
    private final BiConsumer<C, R> targetSetter;
    private final Function<P, Object> transformation;

    TypedNestedTargetBuilder(TypedForEachItemBuilder<S, T, V, C> parent,
                             Function<V, P> sourceAccessor,
                             BiConsumer<C, R> targetSetter,
                             Function<P, Object> transformation) {
        this.parent = parent;
        this.sourceAccessor = sourceAccessor;
        this.targetSetter = targetSetter;
        this.transformation = transformation;
    }

    public TypedForEachItemBuilder<S, T, V, C> end() {
        // Use the properly typed mapping rule
        MappingRule rule = new TypedNestedSimpleMapping<>(sourceAccessor, targetSetter, transformation);
        parent.addNestedRule(rule);
        return parent;
    }
}