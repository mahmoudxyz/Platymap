package dsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionBuilder {
    private final String name;
    private final List<String> parameters = new ArrayList<>();

    FunctionBuilder(String name) {
        this.name = name;
    }

    public FunctionParametersBuilder with(String... params) {
        parameters.addAll(Arrays.asList(params));
        return new FunctionParametersBuilder(name, parameters);
    }
}