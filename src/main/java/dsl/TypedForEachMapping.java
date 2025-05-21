package dsl;

import xyz.mahmoudahmed.adapter.DataNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TypedForEachMapping<S, T, V, C> implements MappingRule {
    private final Function<S, List<V>> collectionAccessor;
    private final BiConsumer<T, List<C>> targetCollectionSetter;
    private final List<MappingRule> nestedRules;

    TypedForEachMapping(Function<S, List<V>> collectionAccessor,
                        BiConsumer<T, List<C>> targetCollectionSetter,
                        List<MappingRule> nestedRules) {
        this.collectionAccessor = collectionAccessor;
        this.targetCollectionSetter = targetCollectionSetter;
        this.nestedRules = new ArrayList<>(nestedRules);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply(MappingContext context, Object target) {
        S source = (S) context.getSourceData();
        List<V> sourceCollection = collectionAccessor.apply(source);

        if (sourceCollection == null || sourceCollection.isEmpty()) {
            return;
        }

        List<C> targetCollection = new ArrayList<>();

        for (V item : sourceCollection) {
            C targetItem = (C) createTargetItem();

            MappingContext itemContext = new MappingContext(item);
            // Copy variables from parent context
            for (Map.Entry<String, Object> entry : context.getVariables().entrySet()) {
                itemContext.setVariable(entry.getKey(), entry.getValue());
            }

            for (MappingRule rule : nestedRules) {
                rule.apply(itemContext, targetItem);
            }

            targetCollection.add(targetItem);
        }

        targetCollectionSetter.accept((T) target, targetCollection);
    }

    private Object createTargetItem() {
        return new DataNode.ObjectNode();
    }
}