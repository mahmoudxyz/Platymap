package xyz.mahmoudahmed.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TypedForEachItemBuilder<S, T, V, C> {
    private final TypedTargetBuilder<S, T> parent;
    private final Function<S, List<V>> collectionAccessor;
    private final BiConsumer<T, List<C>> targetCollectionSetter;
    private final List<MappingRule> nestedRules = new ArrayList<>();

    TypedForEachItemBuilder(TypedTargetBuilder<S, T> parent,
                            Function<S, List<V>> collectionAccessor,
                            BiConsumer<T, List<C>> targetCollectionSetter) {
        this.parent = parent;
        this.collectionAccessor = collectionAccessor;
        this.targetCollectionSetter = targetCollectionSetter;
    }

    public <P> TypedNestedMappingBuilder<S, T, V, C, P> map(Function<V, P> sourceAccessor) {
        return new TypedNestedMappingBuilder<>(this, sourceAccessor);
    }

    public TypedTargetBuilder<S, T> end() {
        parent.addRule(new TypedForEachMapping<>(collectionAccessor, targetCollectionSetter, nestedRules));
        return parent;
    }

    void addNestedRule(MappingRule rule) {
        nestedRules.add(rule);
    }
}
