package dsl;

import xyz.mahmoudahmed.adapter.DataNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ForEachMapping implements MappingRule {
    private final String collectionPath;
    private final String itemName;
    private final List<MappingRule> nestedRules;

    ForEachMapping(String collectionPath, String itemName, List<MappingRule> nestedRules) {
        this.collectionPath = collectionPath;
        this.itemName = itemName;
        this.nestedRules = new ArrayList<>(nestedRules);
    }

    @Override
    public void apply(MappingContext context, Object target) {
        Object collection = context.getValueByPath(collectionPath);
        if (collection == null) {
            return;
        }

        if (collection instanceof DataNode.ArrayNode) {
            DataNode.ArrayNode arrayNode = (DataNode.ArrayNode) collection;
            for (DataNode item : arrayNode.getElements()) {
                context.setVariable(itemName, item);
                for (MappingRule rule : nestedRules) {
                    rule.apply(context, target);
                }
            }
        } else if (collection instanceof Collection) {
            for (Object item : (Collection<?>) collection) {
                context.setVariable(itemName, item);
                for (MappingRule rule : nestedRules) {
                    rule.apply(context, target);
                }
            }
        }
    }
}