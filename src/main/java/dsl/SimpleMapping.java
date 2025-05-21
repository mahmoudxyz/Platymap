package dsl;

import xyz.mahmoudahmed.adapter.DataNode;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;


public class SimpleMapping implements MappingRule {
    private final String sourcePath;
    private final String targetPath;
    private final Function<Object, Object> transformation;
    private final Predicate<Object> condition;

    SimpleMapping(String sourcePath, String targetPath, Function<Object, Object> transformation, Predicate<Object> condition) {
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
            setValueInDataNode((DataNode.ObjectNode) target, targetPath, targetValue);
        }
    }

    static void setValueInDataNode(DataNode.ObjectNode target, String path, Object value) {
        String[] parts = path.split("\\.");
        DataNode.ObjectNode current = target;

        // Navigate to the parent node
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            DataNode child = current.get(part);

            if (child == null || !(child instanceof DataNode.ObjectNode)) {
                DataNode.ObjectNode newNode = new DataNode.ObjectNode();
                current.getProperties().put(part, newNode);
                current = newNode;
            } else {
                current = (DataNode.ObjectNode) child;
            }
        }

        // Set the value on the final node
        String lastPart = parts[parts.length - 1];
        DataNode dataNodeValue = convertToDataNode(value);
        current.getProperties().put(lastPart, dataNodeValue);
    }

    private static DataNode convertToDataNode(Object value) {
        if (value == null) {
            return DataNode.NullValue.INSTANCE;
        } else if (value instanceof DataNode) {
            return (DataNode) value;
        } else if (value instanceof String) {
            return new DataNode.StringValue((String) value);
        } else if (value instanceof Number) {
            return new DataNode.NumberValue((Number) value);
        } else if (value instanceof Boolean) {
            return new DataNode.BooleanValue((Boolean) value);
        } else if (value instanceof Map) {
            DataNode.ObjectNode objNode = new DataNode.ObjectNode();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                objNode.getProperties().put(
                        entry.getKey().toString(),
                        convertToDataNode(entry.getValue())
                );
            }
            return objNode;
        } else if (value instanceof Collection) {
            DataNode.ArrayNode arrayNode = new DataNode.ArrayNode();
            for (Object item : (Collection<?>) value) {
                arrayNode.getElements().add(convertToDataNode(item));
            }
            return arrayNode;
        }

        // For other types, convert to string
        return new DataNode.StringValue(value.toString());
    }
}