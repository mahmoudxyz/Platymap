package dsl;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TypedBranchTargetBuilder<S, T, V, U> {
    private final TypedBranchActionBuilder<S, T> parent;
    private final Function<S, V> sourceAccessor;
    private final BiConsumer<T, U> targetSetter;
    private final Function<V, Object> transformation;

    TypedBranchTargetBuilder(TypedBranchActionBuilder<S, T> parent,
                             Function<S, V> sourceAccessor,
                             BiConsumer<T, U> targetSetter,
                             Function<V, Object> transformation) {
        this.parent = parent;
        this.sourceAccessor = sourceAccessor;
        this.targetSetter = targetSetter;
        this.transformation = transformation;
    }

    public TypedBranchActionBuilder<S, T> end() {
        // Use the properly typed mapping rule
        parent.addAction(new TypedBranchMapping<>(sourceAccessor, targetSetter, transformation, null));
        return parent;
    }
}