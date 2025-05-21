package dsl;

import java.util.function.Function;
import java.util.function.Predicate;

public class MappingBuilder {
    private final TargetBuilder parent;
    private final String sourcePath;
    private Function<Object, Object> transformation;
    private Predicate<Object> condition;

    MappingBuilder(TargetBuilder parent, String sourcePath) {
        this.parent = parent;
        this.sourcePath = sourcePath;
    }

    public TargetPathBuilder to(String targetPath) {
        return new TargetPathBuilder(parent, sourcePath, targetPath, transformation, condition);
    }

    public MappingBuilder transform(Function<Object, Object> transformation) {
        this.transformation = transformation;
        return this;
    }

    public MappingBuilder when(Predicate<Object> condition) {
        this.condition = condition;
        return this;
    }

    // Extension point for custom transformations
    public MappingBuilder uppercase() {
        return transform(value -> value != null ? value.toString().toUpperCase() : null);
    }

    public MappingBuilder lowercase() {
        return transform(value -> value != null ? value.toString().toLowerCase() : null);
    }
}