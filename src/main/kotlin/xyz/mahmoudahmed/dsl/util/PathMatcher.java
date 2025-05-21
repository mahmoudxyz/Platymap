package xyz.mahmoudahmed.dsl.util;

import xyz.mahmoudahmed.adapter.DataNode;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility class for path pattern matching and manipulation.
 */
public class PathMatcher {

    /**
     * Represents a matched path with its value.
     */
    public static class PathMatch {
        private final String path;
        private final DataNode value;
        private final String matchedPattern;

        public PathMatch(String path, DataNode value, String matchedPattern) {
            this.path = path;
            this.value = value;
            this.matchedPattern = matchedPattern;
        }

        public String getPath() {
            return path;
        }

        public DataNode getValue() {
            return value;
        }

        public String getMatchedPattern() {
            return matchedPattern;
        }

        /**
         * Returns the last segment of the path (field name).
         */
        public String getFieldName() {
            int lastDot = path.lastIndexOf('.');
            return lastDot >= 0 ? path.substring(lastDot + 1) : path;
        }
    }

    /**
     * Find all paths in the source that match the pattern.
     *
     * @param source The source object to search in
     * @param pattern The pattern to match against (supports * and **)
     * @return List of matched paths and their values
     */
    public static List<PathMatch> findMatches(DataNode source, String pattern) {
        List<PathMatch> matches = new ArrayList<>();
        findMatchesRecursive(source, "", pattern, matches);
        return matches;
    }

    private static void findMatchesRecursive(DataNode node, String currentPath,
                                             String pattern, List<PathMatch> matches) {
        // Handle single wildcard patterns (a.*.c)
        if (pattern.contains("*") && !pattern.contains("**")) {
            matchSingleWildcard(node, currentPath, pattern, matches);
            return;
        }

        // Handle double wildcard patterns (a.**.c)
        if (pattern.contains("**")) {
            matchDoubleWildcard(node, currentPath, pattern, matches);
            return;
        }

        // Handle exact match patterns
        if (matchesExactPattern(currentPath, pattern)) {
            matches.add(new PathMatch(currentPath, node, pattern));
            return;
        }

        // Recursive traversal for objects and arrays
        if (node instanceof DataNode.ObjectNode) {
            DataNode.ObjectNode obj = (DataNode.ObjectNode) node;
            for (Map.Entry<String, DataNode> entry : obj.getProperties().entrySet()) {
                String path = currentPath.isEmpty() ? entry.getKey() :
                        currentPath + "." + entry.getKey();
                findMatchesRecursive(entry.getValue(), path, pattern, matches);
            }
        } else if (node instanceof DataNode.ArrayNode) {
            DataNode.ArrayNode array = (DataNode.ArrayNode) node;
            for (int i = 0; i < array.getElements().size(); i++) {
                String path = currentPath + "[" + i + "]";
                findMatchesRecursive(array.getElements().get(i), path, pattern, matches);
            }
        }
    }

    private static void matchSingleWildcard(DataNode node, String currentPath,
                                            String pattern, List<PathMatch> matches) {
        if (!(node instanceof DataNode.ObjectNode)) {
            return;
        }

        String[] patternParts = pattern.split("\\.");
        String[] pathParts = currentPath.isEmpty() ? new String[0] : currentPath.split("\\.");

        // Only attempt match when we're at the right depth
        if (patternParts.length - 1 == pathParts.length) {
            DataNode.ObjectNode obj = (DataNode.ObjectNode) node;

            // Check if the pattern matches up to the wildcard
            boolean prefixMatches = true;
            for (int i = 0; i < pathParts.length; i++) {
                if (!patternParts[i].equals("*") && !patternParts[i].equals(pathParts[i])) {
                    prefixMatches = false;
                    break;
                }
            }

            if (prefixMatches) {
                // The last part should be '*'
                if (patternParts[patternParts.length-1].equals("*")) {
                    // All properties match
                    for (Map.Entry<String, DataNode> entry : obj.getProperties().entrySet()) {
                        String path = currentPath.isEmpty() ? entry.getKey() :
                                currentPath + "." + entry.getKey();
                        matches.add(new PathMatch(path, entry.getValue(), pattern));
                    }
                }
            }
        }

        // Continue traversal
        for (Map.Entry<String, DataNode> entry : ((DataNode.ObjectNode) node).getProperties().entrySet()) {
            String path = currentPath.isEmpty() ? entry.getKey() :
                    currentPath + "." + entry.getKey();
            findMatchesRecursive(entry.getValue(), path, pattern, matches);
        }
    }

    private static void matchDoubleWildcard(DataNode node, String currentPath,
                                            String pattern, List<PathMatch> matches) {
        // Handle ** patterns (match anything at any depth)
        if (pattern.equals("**")) {
            // Special case - match everything at any level
            addAllNodesRecursively(node, currentPath, pattern, matches);
            return;
        }

        // Handle prefix.**.suffix patterns
        String[] parts = pattern.split("\\*\\*");
        String prefix = parts[0];
        String suffix = parts.length > 1 ? parts[1] : "";

        // Remove trailing/leading dots
        if (prefix.endsWith(".")) prefix = prefix.substring(0, prefix.length()-1);
        if (suffix.startsWith(".")) suffix = suffix.substring(1);

        // If current path matches the prefix, add all nodes matching the suffix
        if (currentPath.startsWith(prefix) || prefix.isEmpty()) {
            if (suffix.isEmpty()) {
                // Just add this node and all its children
                addAllNodesRecursively(node, currentPath, pattern, matches);
            } else {
                // Find all nodes that end with the suffix
                findAllWithSuffix(node, currentPath, suffix, pattern, matches);
            }
        }

        // Recursive traversal for finding more matches
        if (node instanceof DataNode.ObjectNode) {
            DataNode.ObjectNode obj = (DataNode.ObjectNode) node;
            for (Map.Entry<String, DataNode> entry : obj.getProperties().entrySet()) {
                String path = currentPath.isEmpty() ? entry.getKey() :
                        currentPath + "." + entry.getKey();
                matchDoubleWildcard(entry.getValue(), path, pattern, matches);
            }
        } else if (node instanceof DataNode.ArrayNode) {
            DataNode.ArrayNode array = (DataNode.ArrayNode) node;
            for (int i = 0; i < array.getElements().size(); i++) {
                String path = currentPath + "[" + i + "]";
                matchDoubleWildcard(array.getElements().get(i), path, pattern, matches);
            }
        }
    }

    private static void addAllNodesRecursively(DataNode node, String currentPath,
                                               String pattern, List<PathMatch> matches) {
        matches.add(new PathMatch(currentPath, node, pattern));

        if (node instanceof DataNode.ObjectNode) {
            DataNode.ObjectNode obj = (DataNode.ObjectNode) node;
            for (Map.Entry<String, DataNode> entry : obj.getProperties().entrySet()) {
                String path = currentPath.isEmpty() ? entry.getKey() :
                        currentPath + "." + entry.getKey();
                addAllNodesRecursively(entry.getValue(), path, pattern, matches);
            }
        } else if (node instanceof DataNode.ArrayNode) {
            DataNode.ArrayNode array = (DataNode.ArrayNode) node;
            for (int i = 0; i < array.getElements().size(); i++) {
                String path = currentPath + "[" + i + "]";
                addAllNodesRecursively(array.getElements().get(i), path, pattern, matches);
            }
        }
    }

    private static void findAllWithSuffix(DataNode node, String currentPath,
                                          String suffix, String pattern, List<PathMatch> matches) {
        // Check if this path ends with the suffix
        if (currentPath.endsWith(suffix)) {
            matches.add(new PathMatch(currentPath, node, pattern));
        }

        // Recursive check for objects and arrays
        if (node instanceof DataNode.ObjectNode) {
            DataNode.ObjectNode obj = (DataNode.ObjectNode) node;
            for (Map.Entry<String, DataNode> entry : obj.getProperties().entrySet()) {
                String path = currentPath.isEmpty() ? entry.getKey() :
                        currentPath + "." + entry.getKey();
                findAllWithSuffix(entry.getValue(), path, suffix, pattern, matches);
            }
        } else if (node instanceof DataNode.ArrayNode) {
            DataNode.ArrayNode array = (DataNode.ArrayNode) node;
            for (int i = 0; i < array.getElements().size(); i++) {
                String path = currentPath + "[" + i + "]";
                findAllWithSuffix(array.getElements().get(i), path, suffix, pattern, matches);
            }
        }
    }

    private static boolean matchesExactPattern(String path, String pattern) {
        // Convert glob pattern to regex
        String regex = pattern
                .replace(".", "\\.")
                .replace("*", "[^\\.]*");
        return Pattern.matches(regex, path);
    }

    /**
     * Extracts a field name from a target path pattern.
     * For example: "customer.*.address" with value "phone" becomes "customer.phone.address"
     */
    public static String replaceWildcardWithFieldName(String targetPattern, String fieldName) {
        return targetPattern.replace("*", fieldName);
    }
}