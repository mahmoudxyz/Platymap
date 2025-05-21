package dsl;

import java.util.ArrayList;
import java.util.function.Predicate;

import java.util.*;

public class BranchBuilder {
    private final TargetBuilder parent;
    private final List<ConditionalBranch> branches = new ArrayList<>();

    BranchBuilder(TargetBuilder parent) {
        this.parent = parent;
    }

    public BranchConditionBuilder when(Predicate<Object> condition) {
        return new BranchConditionBuilder(this, condition);
    }

    public BranchActionBuilder otherwise() {
        return new BranchActionBuilder(this, obj -> true);
    }

    public TargetBuilder end() {
        parent.addRule(new BranchMapping(branches));
        return parent;
    }

    void addBranch(ConditionalBranch branch) {
        branches.add(branch);
    }
}