package dsl;

import java.util.function.BiFunction;

/**
 * Builder for nesting operations.
 */
public class NestingBuilder {
    private final TargetBuilder parent;
    private final String sourcePattern;
    private String fieldNameExtractor;
    private BiFunction<String, Object, Object> valueTransformation;

    NestingBuilder(TargetBuilder parent, String sourcePattern) {
        this.parent = parent;
        this.sourcePattern = sourcePattern;
    }

    /**
     * Specify the field to use for extracting the property name.
     */
    public NestingBuilder withFieldNameFrom(String fieldNameExtractor) {
        this.fieldNameExtractor = fieldNameExtractor;
        return this;
    }

    /**
     * Apply a transformation to the values.
     */
    public NestingBuilder transformValues(BiFunction<String, Object, Object> valueTransformation) {
        this.valueTransformation = valueTransformation;
        return this;
    }

    /**
     * Specify the target path and create a collection from matching fields.
     */
    public NestingTargetBuilder asCollection(String collectionName) {
        return new NestingTargetBuilder(parent, sourcePattern, collectionName,
                fieldNameExtractor, valueTransformation, true);
    }

    /**
     * Specify the target path and create an object from matching fields.
     */
    public NestingTargetBuilder asObject(String objectName) {
        return new NestingTargetBuilder(parent, sourcePattern, objectName,
                fieldNameExtractor, valueTransformation, false);
    }

}