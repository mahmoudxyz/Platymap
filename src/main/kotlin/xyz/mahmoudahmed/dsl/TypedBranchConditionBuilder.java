package xyz.mahmoudahmed.dsl;

import java.util.function.Predicate;

public class TypedBranchConditionBuilder<S, T> {
    private final TypedBranchBuilder<S, T> parent;
    private final Predicate<S> condition;

    TypedBranchConditionBuilder(TypedBranchBuilder<S, T> parent, Predicate<S> condition) {
        this.parent = parent;
        this.condition = condition;
    }

    public TypedBranchActionBuilder<S, T> then() {
        return new TypedBranchActionBuilder<>(parent, (Predicate<Object>)(Predicate<?>) condition);
    }
}