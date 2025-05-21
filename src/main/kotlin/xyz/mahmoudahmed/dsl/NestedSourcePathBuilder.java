package xyz.mahmoudahmed.dsl;


import java.util.function.Function;

public class NestedSourcePathBuilder {
    private final NestedMappingBuilder parent;
    private final String sourcePath;
    private Function<Object, Object> transformation;

    NestedSourcePathBuilder(NestedMappingBuilder parent, String sourcePath) {
        this.parent = parent;
        this.sourcePath = sourcePath;
    }

    public NestedTargetPathBuilder to(String targetPath) {
        return new NestedTargetPathBuilder(parent, sourcePath, targetPath, transformation);
    }

    public NestedSourcePathBuilder transform(Function<Object, Object> transformation) {
        this.transformation = transformation;
        return this;
    }
}