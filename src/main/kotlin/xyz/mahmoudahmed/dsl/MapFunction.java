package xyz.mahmoudahmed.dsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class MapFunction {
    private final String name;
    private final List<String> parameters;
    private final Function<Object[], Object> implementation;

    MapFunction(String name, List<String> parameters, Function<Object[], Object> implementation) {
        this.name = name;
        this.parameters = new ArrayList<>(parameters);
        this.implementation = implementation;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public Object execute(Object... args) {
        if (args.length != parameters.size()) {
            throw new IllegalArgumentException(
                    "Function " + name + " expects " + parameters.size() + " arguments, but got " + args.length);
        }
        return implementation.apply(args);
    }
}
