package xyz.mahmoudahmed.dsl;

import java.util.function.Function;

public class BranchTargetBuilder {
    private final BranchActionBuilder parent;
    private final String sourcePath;
    private final String targetPath;
    private final Function<Object, Object> transformation;

    BranchTargetBuilder(BranchActionBuilder parent, String sourcePath, String targetPath,
                        Function<Object, Object> transformation) {
        this.parent = parent;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.transformation = transformation;
    }

    public BranchActionBuilder end() {
        parent.addAction(new SimpleMapping(sourcePath, targetPath, transformation, null));
        return parent;
    }
}
