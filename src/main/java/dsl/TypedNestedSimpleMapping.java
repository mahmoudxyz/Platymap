package dsl;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * MappingRule implementation for typed nested mappings that matches the generic parameters
 * used in TypedNestedTargetBuilder.
 */
public class TypedNestedSimpleMapping<V, P, C, R> implements MappingRule {
    private final Function<V, P> sourceAccessor;
    private final BiConsumer<C, R> targetSetter;
    private final Function<P, Object> transformation;

    /**
     * Creates a new typed nested simple mapping rule.
     *
     * @param sourceAccessor Function to access the source value from a parent object
     * @param targetSetter BiConsumer to set the value on the target object
     * @param transformation Optional transformation to apply to the source value
     */
    public TypedNestedSimpleMapping(
            Function<V, P> sourceAccessor,
            BiConsumer<C, R> targetSetter,
            Function<P, Object> transformation) {
        this.sourceAccessor = sourceAccessor;
        this.targetSetter = targetSetter;
        this.transformation = transformation;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply(MappingContext context, Object target) {
        V source = (V) context.getSourceData();
        C typedTarget = (C) target;

        if (source == null) {
            return;
        }

        P sourceValue = sourceAccessor.apply(source);
        if (sourceValue == null) {
            return;
        }

        Object transformedValue = sourceValue;
        if (transformation != null) {
            transformedValue = transformation.apply(sourceValue);
        }

        // Safe cast - we know the target setter expects type R
        targetSetter.accept(typedTarget, (R) transformedValue);
    }
}