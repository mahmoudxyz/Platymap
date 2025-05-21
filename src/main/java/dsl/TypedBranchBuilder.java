package dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TypedBranchBuilder<S, T> {
    private final TypedTargetBuilder<S, T> parent;
    private final List<ConditionalBranch> branches = new ArrayList<>();

    TypedBranchBuilder(TypedTargetBuilder<S, T> parent) {
        this.parent = parent;
    }

    public TypedBranchConditionBuilder<S, T> when(Predicate<S> condition) {
        return new TypedBranchConditionBuilder<>(this, condition);
    }

    public TypedBranchActionBuilder<S, T> otherwise() {
        return new TypedBranchActionBuilder<>(this, obj -> true);
    }

    public TypedTargetBuilder<S, T> end() {
        parent.addRule(new BranchMapping(branches));
        return parent;
    }

    void addBranch(ConditionalBranch branch) {
        branches.add(branch);
    }
}