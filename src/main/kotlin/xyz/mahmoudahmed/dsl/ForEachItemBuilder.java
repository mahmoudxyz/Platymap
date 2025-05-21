package xyz.mahmoudahmed.dsl;

import java.util.ArrayList;
import java.util.List;

public class ForEachItemBuilder {
    private final TargetBuilder parent;
    private final String collectionPath;
    private final String itemName;
    private final List<MappingRule> nestedRules = new ArrayList<>();

    ForEachItemBuilder(TargetBuilder parent, String collectionPath, String itemName) {
        this.parent = parent;
        this.collectionPath = collectionPath;
        this.itemName = itemName;
    }

    public NestedMappingBuilder create(String targetCollection) {
        return new NestedMappingBuilder(this, targetCollection);
    }

    public TargetBuilder end() {
        parent.addRule(new ForEachMapping(collectionPath, itemName, nestedRules));
        return parent;
    }

    void addNestedRule(MappingRule rule) {
        nestedRules.add(rule);
    }
}