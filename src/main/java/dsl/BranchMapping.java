package dsl;

import java.util.ArrayList;
import java.util.List;

public class BranchMapping implements MappingRule {
    private final List<ConditionalBranch> branches;

    BranchMapping(List<ConditionalBranch> branches) {
        this.branches = new ArrayList<>(branches);
    }

    @Override
    public void apply(MappingContext context, Object target) {
        for (ConditionalBranch branch : branches) {
            if (branch.getCondition().test(context.getSourceData())) {
                for (MappingRule rule : branch.getActions()) {
                    rule.apply(context, target);
                }
                break; // Stop after first matching branch
            }
        }
    }
}