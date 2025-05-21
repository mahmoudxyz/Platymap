package dsl;

public class NestedMappingBuilder {
    private final ForEachItemBuilder parent;
    private final String targetCollection;

    NestedMappingBuilder(ForEachItemBuilder parent, String targetCollection) {
        this.parent = parent;
        this.targetCollection = targetCollection;
    }

    public NestedSourcePathBuilder map(String sourcePath) {
        return new NestedSourcePathBuilder(this, sourcePath);
    }

    public ForEachItemBuilder end() {
        return parent;
    }

    void addNestedRule(MappingRule rule) {
        parent.addNestedRule(rule);
    }

    String getTargetCollection() {
        return targetCollection;
    }
}
