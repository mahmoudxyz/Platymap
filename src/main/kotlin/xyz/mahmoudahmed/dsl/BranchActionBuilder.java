package xyz.mahmoudahmed.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BranchActionBuilder {
    private final BranchBuilder parent;
    private final Predicate<Object> condition;
    private final List<MappingRule> actions = new ArrayList<>();

    BranchActionBuilder(BranchBuilder parent, Predicate<Object> condition) {
        this.parent = parent;
        this.condition = condition;
    }

    public BranchMappingBuilder map(String sourcePath) {
        return new BranchMappingBuilder(this, sourcePath);
    }

    public BranchBuilder endBranch() {
        parent.addBranch(new ConditionalBranch(condition, actions));
        return parent;
    }

    void addAction(MappingRule rule) {
        actions.add(rule);
    }
}
