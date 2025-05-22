package xyz.mahmoudahmed.dsl.util

import xyz.mahmoudahmed.adapter.DataNode
import java.util.regex.Pattern

/**
 * Utility class for path pattern matching and manipulation.
 */
object PathMatcher {

    /**
     * Represents a matched path with its value.
     */
    class PathMatch(
        val path: String,
        val value: DataNode,
        val matchedPattern: String
    ) {
        /**
         * Returns the last segment of the path (field name).
         */
        val fieldName: String
            get() {
                val lastDot = path.lastIndexOf('.')
                return if (lastDot >= 0) path.substring(lastDot + 1) else path
            }
    }

    /**
     * Find all paths in the source that match the pattern.
     *
     * @param source The source object to search in
     * @param pattern The pattern to match against (supports * and **)
     * @return List of matched paths and their values
     */
    fun findMatches(source: DataNode, pattern: String): List<PathMatch> {
        val matches = mutableListOf<PathMatch>()
        findMatchesRecursive(source, "", pattern, matches)
        return matches
    }

    private fun findMatchesRecursive(
        node: DataNode,
        currentPath: String,
        pattern: String,
        matches: MutableList<PathMatch>
    ) {
        // Handle single wildcard patterns (a.*.c)
        if (pattern.contains("*") && !pattern.contains("**")) {
            matchSingleWildcard(node, currentPath, pattern, matches)
            return
        }

        // Handle double wildcard patterns (a.**.c)
        if (pattern.contains("**")) {
            matchDoubleWildcard(node, currentPath, pattern, matches)
            return
        }

        // Handle exact match patterns
        if (matchesExactPattern(currentPath, pattern)) {
            matches.add(PathMatch(currentPath, node, pattern))
            return
        }

        // Recursive traversal for objects and arrays
        when (node) {
            is DataNode.ObjectNode -> {
                for ((key, value) in node.properties) {
                    val path = if (currentPath.isEmpty()) key else "$currentPath.$key"
                    findMatchesRecursive(value, path, pattern, matches)
                }
            }
            is DataNode.ArrayNode -> {
                for (i in node.elements.indices) {
                    val path = "$currentPath[$i]"
                    findMatchesRecursive(node.elements[i], path, pattern, matches)
                }
            }
            else -> { /* do nothing for other node types */ }
        }
    }

    private fun matchSingleWildcard(
        node: DataNode,
        currentPath: String,
        pattern: String,
        matches: MutableList<PathMatch>
    ) {
        if (node !is DataNode.ObjectNode) {
            return
        }

        val patternParts = pattern.split(".")
        val pathParts = if (currentPath.isEmpty()) emptyList() else currentPath.split(".")

        // Only attempt match when we're at the right depth
        if (patternParts.size - 1 == pathParts.size) {
            // Check if the pattern matches up to the wildcard
            val prefixMatches = pathParts.indices.all { i ->
                patternParts[i] == "*" || patternParts[i] == pathParts[i]
            }

            if (prefixMatches) {
                // The last part should be '*'
                if (patternParts.last() == "*") {
                    // All properties match
                    for ((key, value) in node.properties) {
                        val path = if (currentPath.isEmpty()) key else "$currentPath.$key"
                        matches.add(PathMatch(path, value, pattern))
                    }
                }
            }
        }

        // Continue traversal
        for ((key, value) in node.properties) {
            val path = if (currentPath.isEmpty()) key else "$currentPath.$key"
            findMatchesRecursive(value, path, pattern, matches)
        }
    }

    private fun matchDoubleWildcard(
        node: DataNode,
        currentPath: String,
        pattern: String,
        matches: MutableList<PathMatch>
    ) {
        // Handle ** patterns (match anything at any depth)
        if (pattern == "**") {
            // Special case - match everything at any level
            addAllNodesRecursively(node, currentPath, pattern, matches)
            return
        }

        // Handle prefix.**.suffix patterns
        val parts = pattern.split("**")
        var prefix = parts[0]
        var suffix = if (parts.size > 1) parts[1] else ""

        // Remove trailing/leading dots
        if (prefix.endsWith(".")) prefix = prefix.substring(0, prefix.length - 1)
        if (suffix.startsWith(".")) suffix = suffix.substring(1)

        // If current path matches the prefix, add all nodes matching the suffix
        if (currentPath.startsWith(prefix) || prefix.isEmpty()) {
            if (suffix.isEmpty()) {
                // Just add this node and all its children
                addAllNodesRecursively(node, currentPath, pattern, matches)
            } else {
                // Find all nodes that end with the suffix
                findAllWithSuffix(node, currentPath, suffix, pattern, matches)
            }
        }

        // Recursive traversal for finding more matches
        when (node) {
            is DataNode.ObjectNode -> {
                for ((key, value) in node.properties) {
                    val path = if (currentPath.isEmpty()) key else "$currentPath.$key"
                    matchDoubleWildcard(value, path, pattern, matches)
                }
            }
            is DataNode.ArrayNode -> {
                for (i in node.elements.indices) {
                    val path = "$currentPath[$i]"
                    matchDoubleWildcard(node.elements[i], path, pattern, matches)
                }
            }
            else -> { /* do nothing for other node types */ }
        }
    }

    private fun addAllNodesRecursively(
        node: DataNode,
        currentPath: String,
        pattern: String,
        matches: MutableList<PathMatch>
    ) {
        matches.add(PathMatch(currentPath, node, pattern))

        when (node) {
            is DataNode.ObjectNode -> {
                for ((key, value) in node.properties) {
                    val path = if (currentPath.isEmpty()) key else "$currentPath.$key"
                    addAllNodesRecursively(value, path, pattern, matches)
                }
            }
            is DataNode.ArrayNode -> {
                for (i in node.elements.indices) {
                    val path = "$currentPath[$i]"
                    addAllNodesRecursively(node.elements[i], path, pattern, matches)
                }
            }
            else -> { /* do nothing for other node types */ }
        }
    }

    private fun findAllWithSuffix(
        node: DataNode,
        currentPath: String,
        suffix: String,
        pattern: String,
        matches: MutableList<PathMatch>
    ) {
        // Check if this path ends with the suffix
        if (currentPath.endsWith(suffix)) {
            matches.add(PathMatch(currentPath, node, pattern))
        }

        // Recursive check for objects and arrays
        when (node) {
            is DataNode.ObjectNode -> {
                for ((key, value) in node.properties) {
                    val path = if (currentPath.isEmpty()) key else "$currentPath.$key"
                    findAllWithSuffix(value, path, suffix, pattern, matches)
                }
            }
            is DataNode.ArrayNode -> {
                for (i in node.elements.indices) {
                    val path = "$currentPath[$i]"
                    findAllWithSuffix(node.elements[i], path, suffix, pattern, matches)
                }
            }
            else -> { /* do nothing for other node types */ }
        }
    }

    private fun matchesExactPattern(path: String, pattern: String): Boolean {
        // Convert glob pattern to regex
        val regex = pattern
            .replace(".", "\\.")
            .replace("*", "[^\\.]*")
        return Pattern.matches(regex, path)
    }

    /**
     * Extracts a field name from a target path pattern.
     * For example: "customer.*.address" with value "phone" becomes "customer.phone.address"
     */
    fun replaceWildcardWithFieldName(targetPattern: String, fieldName: String): String {
        return targetPattern.replace("*", fieldName)
    }
}