package xyz.mahmoudahmed.dsl;

public class SourceBuilder {
    private final String sourceName;
    private Format sourceFormat = Format.JSON; // Default format

    SourceBuilder(String sourceName) {
        this.sourceName = sourceName;
    }

    public SourceBuilder withFormat(Format format) {
        this.sourceFormat = format;
        return this;
    }

    public TargetBuilder to(String target) {
        return new TargetBuilder(sourceName, sourceFormat, target);
    }
}