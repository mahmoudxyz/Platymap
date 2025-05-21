package xyz.mahmoudahmed.dsl;

import xyz.mahmoudahmed.adapter.DataNode;

import java.util.function.Function;
import java.util.function.Predicate;

public class NestedSimpleMapping implements MappingRule {
    private final String sourcePath;
    private final String targetPath;
    private final Function<Object, Object> transformation;
    private final Predicate<Object> condition;

    NestedSimpleMapping(String sourcePath, String targetPath,
                        Function<Object, Object> transformation, Predicate<Object> condition) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.transformation = transformation;
        this.condition = condition;
    }

    @Override
    public void apply(MappingContext context, Object target) {
        if (condition != null && !condition.test(context.getSourceData())) {
            return;
        }

        Object sourceValue = context.getValueByPath(sourcePath);
        if (sourceValue == null) {
            return;
        }

        Object targetValue = sourceValue;
        if (transformation != null) {
            targetValue = transformation.apply(sourceValue);
        }

        if (target instanceof DataNode.ObjectNode) {
            SimpleMapping.setValueInDataNode((DataNode.ObjectNode) target, targetPath, targetValue);
        }
    }
}