package xyz.mahmoudahmed.dsl;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TypedTargetPathBuilder<S, T, V, U> {
    private final TypedTargetBuilder<S, T> parent;
    private final Function<S, V> sourceAccessor;
    private final BiConsumer<T, U> targetSetter;
    private Function<V, Object> transformation;
    private Predicate<S> condition;

    TypedTargetPathBuilder(TypedTargetBuilder<S, T> parent, Function<S, V> sourceAccessor,
                           BiConsumer<T, U> targetSetter, Function<V, Object> transformation,
                           Predicate<S> condition) {
        this.parent = parent;
        this.sourceAccessor = sourceAccessor;
        this.targetSetter = targetSetter;
        this.transformation = transformation;
        this.condition = condition;
    }

    public TypedTargetBuilder<S, T> using(Function<V, U> function) {
        this.transformation = (Function<V, Object>)(Function<?, ?>) function;
        finalizeMappingRule();
        return parent;
    }

    public TypedTargetBuilder<S, T> when(Predicate<S> condition) {
        this.condition = condition;
        finalizeMappingRule();
        return parent;
    }

    public TypedTargetBuilder<S, T> end() {
        finalizeMappingRule();
        return parent;
    }

    private void finalizeMappingRule() {
        // Use the fully typed mapping rule
        MappingRule rule = new TypedPropertyMapping<>(sourceAccessor, targetSetter, transformation, condition);
        parent.addRule(rule);
    }
}