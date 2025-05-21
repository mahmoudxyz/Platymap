package xyz.mahmoudahmed.dsl;

import xyz.mahmoudahmed.adapter.DataNode;
import xyz.mahmoudahmed.dsl.util.PathMatcher;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Rule that flattens nested structures.
 */
public class FlattenMappingRule implements MappingRule {
    private final String sourcePattern;
    private final String targetPath;
    private final Set<String> exclusions;
    private final Set<String> inclusions;
    private final BiFunction<String, Object, Object> transformation;
    private final String prefix;

    public FlattenMappingRule(String sourcePattern, String targetPath,
                              Set<String> exclusions, Set<String> inclusions,
                              BiFunction<String, Object, Object> transformation,
                              String prefix) {
        this.sourcePattern = sourcePattern;
        this.targetPath = targetPath;
        this.exclusions = exclusions;
        this.inclusions = inclusions;
        this.transformation = transformation;
        this.prefix = prefix;
    }

    @Override
    public void apply(MappingContext context, Object target) {
        if (!(target instanceof DataNode.ObjectNode)) {
            return;
        }

        DataNode sourceNode = (DataNode) context.getSourceData();
        DataNode.ObjectNode targetNode = (DataNode.ObjectNode) target;

        // Find the object to flatten (based on sourcePattern)
        DataNode objectToFlatten = findObjectToFlatten(sourceNode, sourcePattern);
        if (objectToFlatten == null || !(objectToFlatten instanceof DataNode.ObjectNode)) {
            return; // Nothing to flatten
        }

        // Get the target parent node
        DataNode.ObjectNode targetParent = getOrCreateTargetParent(targetNode, targetPath);

        // Flatten the object
        flattenObject((DataNode.ObjectNode) objectToFlatten, targetParent, prefix, "");
    }

    /**
     * Recursively finds the object to flatten based on the source pattern.
     */
    private DataNode findObjectToFlatten(DataNode sourceNode, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return sourceNode;
        }

        String[] parts = pattern.split("\\.", 2);
        String first = parts[0];
        String rest = parts.length > 1 ? parts[1] : "";

        if (sourceNode instanceof DataNode.ObjectNode) {
            DataNode.ObjectNode objNode = (DataNode.ObjectNode) sourceNode;
            DataNode child = objNode.get(first);

            if (child != null) {
                if (rest.isEmpty()) {
                    return child;
                } else {
                    return findObjectToFlatten(child, rest);
                }
            }
        }

        return null;
    }

    /**
     * Gets or creates the target parent node where flattened fields will be placed.
     */
    private DataNode.ObjectNode getOrCreateTargetParent(DataNode.ObjectNode root, String path) {
        if (path == null || path.isEmpty()) {
            return root;
        }

        String[] parts = path.split("\\.");
        DataNode.ObjectNode current = root;

        for (String part : parts) {
            DataNode child = current.get(part);

            if (child == null || !(child instanceof DataNode.ObjectNode)) {
                DataNode.ObjectNode newNode = new DataNode.ObjectNode();
                current.getProperties().put(part, newNode);
                current = newNode;
            } else {
                current = (DataNode.ObjectNode) child;
            }
        }

        return current;
    }

    /**
     * Recursively flattens an object and its nested properties.
     */
    private void flattenObject(DataNode.ObjectNode source, DataNode.ObjectNode target,
                               String keyPrefix, String keyPath) {
        for (Map.Entry<String, DataNode> entry : source.getProperties().entrySet()) {
            String key = entry.getKey();
            DataNode value = entry.getValue();

            // Skip excluded paths
            String fullPath = keyPath.isEmpty() ? key : keyPath + "." + key;
            if (!isPathAllowed(fullPath)) {
                continue;
            }

            // For nested objects, recursively flatten
            if (value instanceof DataNode.ObjectNode) {
                flattenObject((DataNode.ObjectNode) value, target,
                        keyPrefix, fullPath);
            } else {
                // For simple values, add directly with flattened key
                String flatKey = keyPrefix + fullPath.replace('.', '_');

                // Apply transformation if needed
                Object transformedValue = value;
                if (transformation != null) {
                    transformedValue = transformation.apply(fullPath, value);
                }

                target.getProperties().put(flatKey,
                        transformedValue instanceof DataNode
                                ? (DataNode) transformedValue
                                : convertToDataNode(transformedValue));
            }
        }
    }

    private boolean isPathAllowed(String path) {
        // Check exclusions
        for (String exclusion : exclusions) {
            if (path.matches(exclusion.replace("*", ".*"))) {
                return false;
            }
        }

        // Check inclusions if any
        if (!inclusions.isEmpty()) {
            for (String inclusion : inclusions) {
                if (path.matches(inclusion.replace("*", ".*"))) {
                    return true;
                }
            }
            return false;
        }

        // By default, allow the path
        return true;
    }

    private DataNode convertToDataNode(Object value) {
        if (value == null) {
            return DataNode.NullValue.INSTANCE;
        } else if (value instanceof String) {
            return new DataNode.StringValue((String) value);
        } else if (value instanceof Number) {
            return new DataNode.NumberValue((Number) value);
        } else if (value instanceof Boolean) {
            return new DataNode.BooleanValue((Boolean) value);
        } else if (value instanceof DataNode) {
            return (DataNode) value;
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