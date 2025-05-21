package dsl;

public class ForEachBuilder {
    private final TargetBuilder parent;
    private final String collectionPath;

    ForEachBuilder(TargetBuilder parent, String collectionPath) {
        this.parent = parent;
        this.collectionPath = collectionPath;
    }

    public ForEachItemBuilder as(String itemName) {
        return new ForEachItemBuilder(parent, collectionPath, itemName);
    }
}

