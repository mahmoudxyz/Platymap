package xyz.mahmoudahmed.dsl;

import java.util.List;
import java.util.function.Function;

public class FunctionBodyBuilder {
    private final String name;
    private final List<String> parameters;
    private final Function<Object[], Object> implementation;

    FunctionBodyBuilder(String name, List<String> parameters, Function<Object[], Object> implementation) {
        this.name = name;
        this.parameters = parameters;
        this.implementation = implementation;
    }

    public MapFunction build() {
        MapFunction function = new MapFunction(name, parameters, implementation);
        FunctionRegistry.register(function);
        return function;
    }
}