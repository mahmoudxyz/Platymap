package xyz.mahmoudahmed.dsl;

import java.util.List;
import java.util.function.Function;

public class FunctionParametersBuilder {
    private final String name;
    private final List<String> parameters;

    FunctionParametersBuilder(String name, List<String> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public FunctionBodyBuilder body(Function<Object[], Object> implementation) {
        return new FunctionBodyBuilder(name, parameters, implementation);
    }
}
