package dsl;

import java.util.Set;
import java.util.function.BiFunction;

import dsl.util.PathMatcher;
import xyz.mahmoudahmed.adapter.DataNode;

import java.util.*;

/**
 * Rule that maps multiple source fields based on a pattern.
 */
public class BulkMappingRule implements MappingRule {
    private final String sourcePattern;
    private final String targetPath;
    private final Set<String> exclusions;
    private final Set<String> inclusions;
    private final BiFunction<String, Object, Object> transformation;
    private final boolean preserveStructure;

    public BulkMappingRule(String sourcePattern, String targetPath,
                           Set<String> exclusions, Set<String> inclusions,
                           BiFunction<String, Object, Object> transformation,
                           boolean preserveStructure) {
        this.sourcePattern = sourcePattern;
        this.targetPath = targetPath;
        this.exclusions = exclusions;
        this.inclusions = inclusions;
        this.transformation = transformation;
        this.preserveStructure = preserveStructure;
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

        // Apply inclusions/exclusions
        List<PathMatcher.PathMatch> filteredMatches = new ArrayList<>();
        for (PathMatcher.PathMatch match : matches) {
            String path = match.getPath();

            // Skip excluded paths
            if (isExcluded(path)) {
                continue;
            }

            // Only include paths in the inclusion list, if any
            if (!inclusions.isEmpty() && !isIncluded(path)) {
                continue;
            }

            filteredMatches.add(match);
        }

        // Process matches based on structure preservation
        if (preserveStructure) {
            applyPreservingStructure(filteredMatches, targetNode);
        } else {
            applyFlatStructure(filteredMatches, targetNode);
        }
    }

    private boolean isExcluded(String path) {
        for (String exclusion : exclusions) {
            if (path.matches(exclusion.replace("*", ".*"))) {
                return true;
            }
        }
        return false;
    }

    private boolean isIncluded(String path) {
        for (String inclusion : inclusions) {
            if (path.matches(inclusion.replace("*", ".*"))) {
                return true;
            }
        }
        return false;
    }

    private void applyPreservingStructure(List<PathMatcher.PathMatch> matches, DataNode.ObjectNode targetNode) {
        // Group matches by their path structure to maintain hierarchy
        for (PathMatcher.PathMatch match : matches) {
            String relativePath = getRelativePath(match.getPath(), sourcePattern);

            // Transform the value if needed
            Object value = match.getValue();
            if (transformation != null) {
                value = transformation.apply(match.getFieldName(), value);
            }

            // Set in the target, preserving the structure
            String fullTargetPath = targetPath;
            if (!relativePath.isEmpty()) {
                fullTargetPath = targetPath + "." + relativePath;
            }

            setValueInPath(targetNode, fullTargetPath, value);
        }
    }

    private void applyFlatStructure(List<PathMatcher.PathMatch> matches, DataNode.ObjectNode targetNode) {
        // For a flat structure, just put all fields directly under the target path
        for (PathMatcher.PathMatch match : matches) {
            // Transform the value if needed
            Object value = match.getValue();
            if (transformation != null) {
                value = transformation.apply(match.getFieldName(), value);
            }

            // Set in the target at the target path + field name
            String fieldPath = targetPath + "." + match.getFieldName();
            setValueInPath(targetNode, fieldPath, value);
        }
    }

    private String getRelativePath(String fullPath, String patternPath) {
        // Remove the base path from the full path to get the relative part
        if (patternPath.endsWith(".*")) {
            String basePath = patternPath.substring(0, patternPath.length() - 2);
            if (fullPath.startsWith(basePath)) {
                return fullPath.substring(basePath.length() + 1);
            }
        } else if (patternPath.endsWith(".**")) {
            String basePath = patternPath.substring(0, patternPath.length() - 3);
            if (fullPath.startsWith(basePath)) {
                return fullPath.substring(basePath.length() + 1);
            }
        }

        // If no match or we're dealing with a more complex pattern,
        // just return the field name
        int lastDot = fullPath.lastIndexOf('.');
        return lastDot >= 0 ? fullPath.substring(lastDot + 1) : fullPath;
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
