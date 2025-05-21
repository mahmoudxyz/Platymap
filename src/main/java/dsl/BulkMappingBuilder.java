package dsl;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Builder for bulk mapping operations.
 */
public class BulkMappingBuilder {
    private final TargetBuilder parent;
    private final String sourcePattern;
    private final Set<String> exclusions = new HashSet<>();
    private final Set<String> inclusions = new HashSet<>();
    private BiFunction<String, Object, Object> transformation;
    private boolean preserveStructure = true;
    private boolean flatten = false;
    private String flattenPrefix = "";

    BulkMappingBuilder(TargetBuilder parent, String sourcePattern) {
        this.parent = parent;
        this.sourcePattern = sourcePattern;
    }

    /**
     * Exclude specific paths from the bulk mapping.
     */
    public BulkMappingBuilder excluding(String... paths) {
        Collections.addAll(exclusions, paths);
        return this;
    }

    /**
     * Include only specific paths from the bulk mapping.
     */
    public BulkMappingBuilder including(String... paths) {
        Collections.addAll(inclusions, paths);
        return this;
    }

    /**
     * Apply a transformation to each match.
     */
    public BulkMappingBuilder transformEach(BiFunction<String, Object, Object> transformation) {
        this.transformation = transformation;
        return this;
    }

    /**
     * Apply a simple value transformation to each match.
     */
    public BulkMappingBuilder transformValues(Function<Object, Object> valueTransformation) {
        this.transformation = (key, value) -> valueTransformation.apply(value);
        return this;
    }

    /**
     * Flatten the structure instead of preserving hierarchy.
     */
    public BulkMappingBuilder flatten() {
        this.preserveStructure = false;
        this.flatten = true;
        return this;
    }

    /**
     * Flatten with a prefix for field names.
     */
    public BulkMappingBuilder flattenWithPrefix(String prefix) {
        this.preserveStructure = false;
        this.flatten = true;
        this.flattenPrefix = prefix;
        return this;
    }

    /**
     * Preserve the nested structure when mapping.
     */
    public BulkMappingBuilder preserveStructure() {
        this.preserveStructure = true;
        this.flatten = false;
        return this;
    }

    /**
     * Specify the target path.
     */
    public BulkTargetPathBuilder to(String targetPath) {
        return new BulkTargetPathBuilder(parent, sourcePattern, targetPath,
                exclusions, inclusions, transformation,
                preserveStructure, flatten, flattenPrefix);
    }
}
