package dsl;

import dsl.util.PathMatcher;
import xyz.mahmoudahmed.adapter.DataNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Rule that nests flat fields into a structured object or array.
 */
public class NestingMappingRule implements MappingRule {
    private final String sourcePattern;
    private final String targetPath;
    private final String structureName;
    private final String fieldNameExtractor;
    private final BiFunction<String, Object, Object> valueTransformation;
    private final boolean isCollection;

    public NestingMappingRule(String sourcePattern, String targetPath, String structureName,
                              String fieldNameExtractor, BiFunction<String, Object, Object> valueTransformation,
                              boolean isCollection) {
        this.sourcePattern = sourcePattern;
        this.targetPath = targetPath;
        this.structureName = structureName;
        this.fieldNameExtractor = fieldNameExtractor;
        this.valueTransformation = valueTransformation;
        this.isCollection = isCollection;
    }

    @Override
    public void apply(MappingContext context, Object target) {
        if (!(target instanceof DataNode.ObjectNode)) {
            return;
        }

        DataNode sourceNode = (DataNode) context.getSourceData();
        DataNode.ObjectNode targetNode = (DataNode.ObjectNode) target;

        // Find all paths that match the pattern
        List<PathMatcher.PathMatch> matches = PathMatcher.findMatches(sourceNode, sourcePattern);

        // Create the nested structure
        if (isCollection) {
            createNestedCollection(matches, targetNode);
        } else {
            createNestedObject(matches, targetNode);
        }
    }

    private void createNestedCollection(List<PathMatcher.PathMatch> matches, DataNode.ObjectNode targetNode) {
        // Create a collection of items
        DataNode.ArrayNode arrayNode = new DataNode.ArrayNode();

        for (PathMatcher.PathMatch match : matches) {
            DataNode.ObjectNode itemNode = new DataNode.ObjectNode();

            // Extract the field name to use as a key or include in the item
            String fieldName = extractFieldName(match.getPath());

            // Transform the value if needed
            Object value = match.getValue();
            if (valueTransformation != null) {
                value = valueTransformation.apply(fieldName, value);
            }

            // Add the field name if specified
            if (fieldNameExtractor != null && !fieldNameExtractor.isEmpty()) {
                itemNode.getProperties().put(fieldNameExtractor, new DataNode.StringValue(fieldName));
            }

            // Add the value
            String valueField = fieldNameExtractor != null && !fieldNameExtractor.isEmpty() ?
                    "value" : fieldName;

            if (value instanceof DataNode) {
                itemNode.getProperties().put(valueField, (DataNode) value);
            } else {
                itemNode.getProperties().put(valueField, convertToDataNode(value));
            }

            // Add to collection
            arrayNode.getElements().add(itemNode);
        }

        // Set the array in the target
        setValueInPath(targetNode, targetPath + "." + structureName, arrayNode);
    }

    private void createNestedObject(List<PathMatcher.PathMatch> matches, DataNode.ObjectNode targetNode) {
        // Create a single object with all the fields
        DataNode.ObjectNode nestedObject = new DataNode.ObjectNode();

        for (PathMatcher.PathMatch match : matches) {
            // Extract the field name
            String fieldName = extractFieldName(match.getPath());

            // Transform the value if needed
            Object value = match.getValue();
            if (valueTransformation != null) {
                value = valueTransformation.apply(fieldName, value);
            }

            // Add to nested object
            if (value instanceof DataNode) {
                nestedObject.getProperties().put(fieldName, (DataNode) value);
            } else {
                nestedObject.getProperties().put(fieldName, convertToDataNode(value));
            }
        }

        // Set the object in the target
        setValueInPath(targetNode, targetPath + "." + structureName, nestedObject);
    }

    private String extractFieldName(String path) {
        // Extract the part of the path that matches the wildcard in the pattern
        if (sourcePattern.contains("*")) {
            String prefix = sourcePattern.substring(0, sourcePattern.indexOf("*"));
            String suffix = sourcePattern.substring(sourcePattern.indexOf("*") + 1);

            if (path.startsWith(prefix) &&
                    (suffix.isEmpty() || path.endsWith(suffix))) {

                int startIndex = prefix.length();
                int endIndex = suffix.isEmpty() ? path.length() : path.length() - suffix.length();
                return path.substring(startIndex, endIndex);
            }
        }

        // Default: just use the last segment of the path
        int lastDot = path.lastIndexOf('.');
        return lastDot >= 0 ? path.substring(lastDot + 1) : path;
    }

    private void setValueInPath(DataNode.ObjectNode targetNode, String path, Object value) {
        String[] parts = path.split("\\.");
        DataNode.ObjectNode current = targetNode;

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

        // Convert value to DataNode if needed
        if (value instanceof DataNode) {
            current.getProperties().put(lastPart, (DataNode) value);
        } else {
            current.getProperties().put(lastPart, convertToDataNode(value));
        }
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
