package xyz.mahmoudahmed.dsl;

import java.util.function.Function;

public class NestedTargetPathBuilder {
    private final NestedMappingBuilder parent;
    private final String sourcePath;
    private final String targetPath;
    private final Function<Object, Object> transformation;

    NestedTargetPathBuilder(NestedMappingBuilder parent, String sourcePath, String targetPath,
                            Function<Object, Object> transformation) {
        this.parent = parent;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.transformation = transformation;
    }

    public NestedMappingBuilder end() {
        MappingRule rule = new NestedSimpleMapping(
                sourcePath,
                parent.getTargetCollection() + (targetPath.startsWith(".") ? "" : ".") + targetPath,
                transformation,
                null
        );
        parent.addNestedRule(rule);
        return parent;
    }
}
