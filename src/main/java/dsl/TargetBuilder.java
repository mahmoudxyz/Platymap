package dsl;

import java.util.ArrayList;
import java.util.List;

public class TargetBuilder {
    private final String sourceName;
    private final Format sourceFormat;
    private final String targetName;
    private Format targetFormat = Format.JSON; // Default format
    private final List<MappingRule> rules = new ArrayList<>();

    TargetBuilder(String sourceName, Format sourceFormat, String targetName) {
        this.sourceName = sourceName;
        this.sourceFormat = sourceFormat;
        this.targetName = targetName;
    }

    public TargetBuilder withFormat(Format format) {
        this.targetFormat = format;
        return this;
    }

    public MappingBuilder map(String sourcePath) {
        return new MappingBuilder(this, sourcePath);
    }

    /**
     * Map all fields that match a pattern.
     */
    public BulkMappingBuilder mapAll(String sourcePattern) {
        return new BulkMappingBuilder(this, sourcePattern);
    }

    /**
     * Map all fields except those that match the excluded patterns.
     */
    public BulkMappingBuilder mapAllExcept(String sourcePattern) {
        return new BulkMappingBuilder(this, sourcePattern);
    }

    /**
     * Group fields that match a pattern into a nested structure.
     */
    public NestingBuilder nest(String sourcePattern) {
        return new NestingBuilder(this, sourcePattern);
    }

    /**
     * Flatten a nested structure.
     */
    public BulkMappingBuilder flatten(String sourcePath) {
        return new BulkMappingBuilder(this, sourcePath).flatten();
    }

    public ForEachBuilder forEach(String collectionPath) {
        return new ForEachBuilder(this, collectionPath);
    }

    public BranchBuilder branch() {
        return new BranchBuilder(this);
    }

    // Build method to create the final mapping
    public Mapping build() {
        return new Mapping(sourceName, sourceFormat, targetName, targetFormat, rules);
    }

    // Internal method to add a rule
    void addRule(MappingRule rule) {
        rules.add(rule);
    }
}