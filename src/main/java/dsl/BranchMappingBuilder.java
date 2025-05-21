package dsl;

import java.util.function.Function;

public class BranchMappingBuilder {
    private final BranchActionBuilder parent;
    private final String sourcePath;
    private Function<Object, Object> transformation;

    BranchMappingBuilder(BranchActionBuilder parent, String sourcePath) {
        this.parent = parent;
        this.sourcePath = sourcePath;
    }

    public BranchTargetBuilder to(String targetPath) {
        return new BranchTargetBuilder(parent, sourcePath, targetPath, transformation);
    }

    public BranchMappingBuilder transform(Function<Object, Object> transformation) {
        this.transformation = transformation;
        return this;
    }
}