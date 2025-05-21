package dsl;

import java.util.Set;
import java.util.function.BiFunction;

/**
 * Builder for specifying the target of bulk mappings.
 */
public class BulkTargetPathBuilder {
    private final TargetBuilder parent;
    private final String sourcePattern;
    private final String targetPath;
    private final Set<String> exclusions;
    private final Set<String> inclusions;
    private final BiFunction<String, Object, Object> transformation;
    private final boolean preserveStructure;
    private final boolean flatten;
    private final String flattenPrefix;

    BulkTargetPathBuilder(TargetBuilder parent, String sourcePattern, String targetPath,
                          Set<String> exclusions, Set<String> inclusions,
                          BiFunction<String, Object, Object> transformation,
                          boolean preserveStructure, boolean flatten, String flattenPrefix) {
        this.parent = parent;
        this.sourcePattern = sourcePattern;
        this.targetPath = targetPath;
        this.exclusions = exclusions;
        this.inclusions = inclusions;
        this.transformation = transformation;
        this.preserveStructure = preserveStructure;
        this.flatten = flatten;
        this.flattenPrefix = flattenPrefix;
    }

    /**
     * Finalize the bulk mapping rule.
     */
    public TargetBuilder end() {
        MappingRule rule;
        if (flatten) {
            rule = new FlattenMappingRule(sourcePattern, targetPath, exclusions,
                    inclusions, transformation, flattenPrefix);
        } else if (preserveStructure) {
            rule = new BulkMappingRule(sourcePattern, targetPath, exclusions,
                    inclusions, transformation, true);
        } else {
            rule = new BulkMappingRule(sourcePattern, targetPath, exclusions,
                    inclusions, transformation, false);
        }
        parent.addRule(rule);
        return parent;
    }
}