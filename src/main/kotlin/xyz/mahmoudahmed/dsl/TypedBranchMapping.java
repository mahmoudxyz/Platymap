package xyz.mahmoudahmed.dsl;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A properly typed version of SimpleMapping that preserves the generic parameters
 * for the target setter.
 */
public class TypedBranchMapping<S, T, V, U> implements MappingRule {
    private final Function<S, V> sourceAccessor;
    private final BiConsumer<T, U> targetSetter;
    private final Function<V, Object> transformation;
    private final Predicate<S> condition;

    public TypedBranchMapping(Function<S, V> sourceAccessor, BiConsumer<T, U> targetSetter,
                              Function<V, Object> transformation, Predicate<S> condition) {
        this.sourceAccessor = sourceAccessor;
        this.targetSetter = targetSetter;
        this.transformation = transformation;
        this.condition = condition;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply(MappingContext context, Object target) {
        S source = (S) context.getSourceData();
        if (condition != null && !condition.test(source)) {
            return;
        }

        V sourceValue = sourceAccessor.apply(source);
        if (sourceValue == null) {
            return;
        }

        Object transformedValue = sourceValue;
        if (transformation != null) {
            transformedValue = transformation.apply(sourceValue);
        }

        // Type cast here - we know T is the type of our target
        targetSetter.accept((T) target, (U) transformedValue);
    }
}