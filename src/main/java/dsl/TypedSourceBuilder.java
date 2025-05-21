package dsl;

public class TypedSourceBuilder<S> {
    private final Class<S> sourceClass;
    private Format sourceFormat = Format.JAVA_BEAN;

    TypedSourceBuilder(Class<S> sourceClass) {
        this.sourceClass = sourceClass;
    }

    public TypedSourceBuilder<S> withFormat(Format format) {
        this.sourceFormat = format;
        return this;
    }

    public <T> TypedTargetBuilder<S, T> to(Class<T> targetClass) {
        return new TypedTargetBuilder<>(sourceClass, sourceFormat, targetClass);
    }
}