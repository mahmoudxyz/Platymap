package xyz.mahmoudahmed.dsl;

import xyz.mahmoudahmed.adapter.InputAdapterService;
import xyz.mahmoudahmed.dsl.Format;
import xyz.mahmoudahmed.dsl.FunctionBuilder;
import xyz.mahmoudahmed.dsl.SourceBuilder;
import xyz.mahmoudahmed.dsl.TypedSourceBuilder;
import xyz.mahmoudahmed.format.FormatType;


public class Platymap {
    private static final InputAdapterService adapterService = new InputAdapterService();

    // Entry point factory methods
    public static SourceBuilder flow(String source) {
        return new SourceBuilder(source);
    }

    public static <S> TypedSourceBuilder<S> flow(Class<S> sourceClass) {
        return new TypedSourceBuilder<>(sourceClass);
    }

    public static FunctionBuilder function(String name) {
        return new FunctionBuilder(name);
    }

    // Utility method to convert Format enum to Kotlin's FormatType
    static FormatType toKotlinFormat(Format format) {
        switch (format) {
            case JSON: return FormatType.JSON;
            case XML: return FormatType.XML;
            case CSV: return FormatType.CSV;
            case YAML: return FormatType.YAML;
            case PROPERTIES: return FormatType.PROPERTIES;
            default: return FormatType.UNKNOWN;
        }
    }


    // Get the adapter service instance
    public static InputAdapterService getAdapterService() {
        return adapterService;
    }
}