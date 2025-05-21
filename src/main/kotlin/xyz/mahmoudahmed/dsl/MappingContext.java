package xyz.mahmoudahmed.dsl;

import xyz.mahmoudahmed.adapter.DataNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappingContext {
    private final Object sourceData;
    private final Map<String, Object> variables = new HashMap<>();

    MappingContext(Object sourceData) {
        this.sourceData = sourceData;
    }

    public Object getSourceData() {
        return sourceData;
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public Object getValueByPath(String path) {
        if (path.startsWith("'") && path.endsWith("'")) {
            // Literal value
            return path.substring(1, path.length() - 1);
        } else if (path.startsWith("$")) {
            // Variable reference
            return variables.get(path.substring(1));
        } else {
            // Path in source data
            return extractValueByPath(sourceData, path);
        }
    }

    private Object extractValueByPath(Object source, String path) {
        if (source instanceof DataNode) {
            return extractFromDataNode((DataNode) source, path);
        } else {
            // For Java beans or other objects, use reflection or other means
            // to extract the value
            return null; // Not implemented for brevity
        }
    }

    private Object extractFromDataNode(DataNode node, String path) {
        if (path == null || path.isEmpty()) {
            return node;
        }

        String[] parts = path.split("\\.");
        DataNode current = node;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            // Handle array indexing
            if (part.contains("[") && part.endsWith("]")) {
                int bracketPos = part.indexOf("[");
                String fieldName = part.substring(0, bracketPos);
                String indexStr = part.substring(bracketPos + 1, part.length() - 1);

                try {
                    int index = Integer.parseInt(indexStr);
                    if (current instanceof DataNode.ObjectNode) {
                        DataNode arrayNode = ((DataNode.ObjectNode) current).get(fieldName);
                        if (arrayNode instanceof DataNode.ArrayNode) {
                            current = ((DataNode.ArrayNode) arrayNode).get(index);
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                // Regular property access
                if (current instanceof DataNode.ObjectNode) {
                    current = ((DataNode.ObjectNode) current).get(part);
                } else {
                    return null;
                }
            }
        }

        return current;
    }
}
