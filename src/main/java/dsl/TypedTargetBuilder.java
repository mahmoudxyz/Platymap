package dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TypedTargetBuilder<S, T> {
    private final Class<S> sourceClass;
    private final Format sourceFormat;
    private final Class<T> targetClass;
    private Format targetFormat = Format.JAVA_BEAN;
    private final List<MappingRule> rules = new ArrayList<>();

    TypedTargetBuilder(Class<S> sourceClass, Format sourceFormat, Class<T> targetClass) {
        this.sourceClass = sourceClass;
        this.sourceFormat = sourceFormat;
        this.targetClass = targetClass;
    }

    public TypedTargetBuilder<S, T> withFormat(Format format) {
        this.targetFormat = format;
        return this;
    }

    public <V> TypedMappingBuilder<S, T, V> map(Function<S, V> sourceAccessor) {
        return new TypedMappingBuilder<>(this, sourceAccessor);
    }

    public <V> TypedForEachBuilder<S, T, V> forEach(Function<S, List<V>> collectionAccessor) {
        return new TypedForEachBuilder<>(this, collectionAccessor);
    }

    public TypedBranchBuilder<S, T> branch() {
        return new TypedBranchBuilder<>(this);
    }

    // Internal method to add a rule
    void addRule(MappingRule rule) {
        rules.add(rule);
    }

    // Build method to create the final mapping
    public TypedMapping<S, T> build() {
        return new TypedMapping<>(sourceClass, sourceFormat, targetClass, targetFormat, rules);
    }
}