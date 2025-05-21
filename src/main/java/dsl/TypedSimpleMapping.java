package dsl;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TypedSimpleMapping<S, V> implements MappingRule {
    private final Function<S, V> sourceAccessor;
    private final BiConsumer<Object, Object> targetSetter;
    private final Function<V, Object> transformation;
    private final Predicate<S> condition;

    TypedSimpleMapping(Function<S, V> sourceAccessor, BiConsumer<Object, Object> targetSetter,
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

        Object targetValue = sourceValue;
        if (transformation != null) {
            targetValue = transformation.apply(sourceValue);
        }

        targetSetter.accept(target, targetValue);
    }
}
