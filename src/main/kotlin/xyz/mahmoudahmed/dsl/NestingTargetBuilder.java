package xyz.mahmoudahmed.dsl;

import java.util.function.BiFunction;

/**
 * Builder for specifying the target of nesting operations.
 */
public class NestingTargetBuilder {
    private final TargetBuilder parent;
    private final String sourcePattern;
    private final String structureName;
    private final String fieldNameExtractor;
    private final BiFunction<String, Object, Object> valueTransformation;
    private final boolean isCollection;

    NestingTargetBuilder(TargetBuilder parent, String sourcePattern, String structureName,
                         String fieldNameExtractor, BiFunction<String, Object, Object> valueTransformation,
                         boolean isCollection) {
        this.parent = parent;
        this.sourcePattern = sourcePattern;
        this.structureName = structureName;
        this.fieldNameExtractor = fieldNameExtractor;
        this.valueTransformation = valueTransformation;
        this.isCollection = isCollection;
    }

    /**
     * Specify the target path for the nested structure.
     */
    public TargetBuilder to(String targetPath) {
        MappingRule rule = new NestingMappingRule(sourcePattern, targetPath, structureName,
                fieldNameExtractor, valueTransformation, isCollection);
        parent.addRule(rule);
        return parent;
    }
}