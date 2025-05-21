package xyz.mahmoudahmed.dsl;

import xyz.mahmoudahmed.adapter.DataNode;

import java.util.ArrayList;
import java.util.List;

public class TypedMapping<S, T> {
    private final Class<S> sourceClass;
    private final Format sourceFormat;
    private final Class<T> targetClass;
    private final Format targetFormat;
    private final List<MappingRule> rules;

    TypedMapping(Class<S> sourceClass, Format sourceFormat, Class<T> targetClass, Format targetFormat, List<MappingRule> rules) {
        this.sourceClass = sourceClass;
        this.sourceFormat = sourceFormat;
        this.targetClass = targetClass;
        this.targetFormat = targetFormat;
        this.rules = new ArrayList<>(rules);
    }

    @SuppressWarnings("unchecked")
    public T execute(S source) {
        try {
            // For JAVA_BEAN format, create a new instance of the target class
            T target;
            if (targetFormat == Format.JAVA_BEAN) {
                target = targetClass.getDeclaredConstructor().newInstance();
            } else {
                // For other formats, create a DataNode
                DataNode.ObjectNode targetNode = new DataNode.ObjectNode();
                target = (T) targetNode;
            }

            // Create the mapping context
            MappingContext context = new MappingContext(source);

            // Apply all the mapping rules
            for (MappingRule rule : rules) {
                rule.apply(context, target);
            }

            return target;
        } catch (Exception e) {
            throw new MappingExecutionException("Error executing mapping", e);
        }
    }
}
