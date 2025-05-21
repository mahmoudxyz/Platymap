package xyz.mahmoudahmed.dsl;

import java.util.function.Function;
import java.util.function.Predicate;

public class TargetPathBuilder {
    private final TargetBuilder parent;
    private final String sourcePath;
    private final String targetPath;
    private Function<Object, Object> transformation;
    private Predicate<Object> condition;

    TargetPathBuilder(TargetBuilder parent, String sourcePath, String targetPath,
                      Function<Object, Object> transformation, Predicate<Object> condition) {
        this.parent = parent;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.transformation = transformation;
        this.condition = condition;
    }

    public TargetBuilder using(Function<Object, Object> function) {
        this.transformation = function;
        finalizeMappingRule();
        return parent;
    }

    public TargetBuilder when(Predicate<Object> condition) {
        this.condition = condition;
        finalizeMappingRule();
        return parent;
    }

    // Finalize and return to parent
    public TargetBuilder end() {
        finalizeMappingRule();
        return parent;
    }

    private void finalizeMappingRule() {
        MappingRule rule = new SimpleMapping(sourcePath, targetPath, transformation, condition);
        parent.addRule(rule);
    }
}