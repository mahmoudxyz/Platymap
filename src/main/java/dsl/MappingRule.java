package dsl;

public interface MappingRule {
    void apply(MappingContext context, Object target);
}