package xyz.mahmoudahmed.dsl;

import java.util.function.Predicate;

public class BranchConditionBuilder {
    private final BranchBuilder parent;
    private final Predicate<Object> condition;

    BranchConditionBuilder(BranchBuilder parent, Predicate<Object> condition) {
        this.parent = parent;
        this.condition = condition;
    }

    public BranchActionBuilder then() {
        return new BranchActionBuilder(parent, condition);
    }
}