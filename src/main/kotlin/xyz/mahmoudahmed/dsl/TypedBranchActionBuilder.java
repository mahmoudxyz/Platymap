package xyz.mahmoudahmed.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class TypedBranchActionBuilder<S, T> {
    private final TypedBranchBuilder<S, T> parent;
    private final Predicate<Object> condition;
    private final List<MappingRule> actions = new ArrayList<>();

    TypedBranchActionBuilder(TypedBranchBuilder<S, T> parent, Predicate<Object> condition) {
        this.parent = parent;
        this.condition = condition;
    }

    public <V> TypedBranchMappingBuilder<S, T, V> map(Function<S, V> sourceAccessor) {
        return new TypedBranchMappingBuilder<>(this, sourceAccessor);
    }

    public TypedBranchBuilder<S, T> endBranch() {
        parent.addBranch(new ConditionalBranch(condition, actions));
        return parent;
    }

    void addAction(MappingRule rule) {
        actions.add(rule);
    }
}