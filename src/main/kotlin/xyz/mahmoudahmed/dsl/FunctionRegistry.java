package xyz.mahmoudahmed.dsl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionRegistry {
    private static final Map<String, MapFunction> functions = new HashMap<>();

    public static void register(MapFunction function) {
        functions.put(function.getName(), function);
    }

    public static MapFunction get(String name) {
        MapFunction function = functions.get(name);
        if (function == null) {
            throw new IllegalArgumentException("Function not found: " + name);
        }
        return function;
    }

    public static Object call(String name, Object... args) {
        return get(name).execute(args);
    }

    static {
        // Register built-in functions
        register(new MapFunction("trim", List.of("value"),
                args -> args[0] instanceof String ? ((String) args[0]).trim() : args[0]));

        register(new MapFunction("concat", List.of("value1", "value2"),
                args -> String.valueOf(args[0]) + String.valueOf(args[1])));

        register(new MapFunction("upperCase", List.of("value"),
                args -> args[0] instanceof String ? ((String) args[0]).toUpperCase() : String.valueOf(args[0]).toUpperCase()));

        register(new MapFunction("lowerCase", List.of("value"),
                args -> args[0] instanceof String ? ((String) args[0]).toLowerCase() : String.valueOf(args[0]).toLowerCase()));
    }
}