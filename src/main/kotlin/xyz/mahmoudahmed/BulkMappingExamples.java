package xyz.mahmoudahmed;

import xyz.mahmoudahmed.adapter.DataNode;
import xyz.mahmoudahmed.dsl.Format;
import xyz.mahmoudahmed.dsl.Mapping;
import xyz.mahmoudahmed.dsl.Platymap;

public class BulkMappingExamples {

    public static void main(String[] args) {
        // Example 1: Map all fields at a specific level
        wildcardMapping();

        // Example 2: Flatten nested structures
        flattenStructureMapping();

        // Example 3: Nest flat fields into a structured object
        nestFieldsMapping();

        // Example 4: Field exclusion and transformation
        transformationMapping();
    }

    public static void wildcardMapping() {
        // Map all immediate fields from customer to profile
        Mapping customerMapping = Platymap.flow("customer")
                .withFormat(Format.JSON)
                .to("profile")
                .withFormat(Format.JSON)
                .mapAll("customer.*").to("profile").end()
                .build();

        String jsonInput = "{ \"customer\": { " +
                "  \"firstName\": \"John\", " +
                "  \"lastName\": \"Doe\", " +
                "  \"email\": \"john@example.com\", " +
                "  \"address\": { " +
                "    \"street\": \"123 Main St\", " +
                "    \"city\": \"Anytown\" " +
                "  } " +
                "} }";

        String result = customerMapping.executeToJson(jsonInput);
        System.out.println("Wildcard Mapping Result:");
        System.out.println(result);
    }

    public static void flattenStructureMapping() {
        // Flatten a nested structure
        Mapping addressMapping = Platymap.flow("customer")
                .withFormat(Format.JSON)
                .to("profile")
                .withFormat(Format.JSON)
                .flatten("customer.address").flattenWithPrefix("addr_").to("profile").end()
                .map("customer.firstName").to("profile.name").end()
                .map("customer.lastName").to("profile.surname").end()
                .build();

        String jsonInput = "{ \"customer\": { " +
                "  \"firstName\": \"John\", " +
                "  \"lastName\": \"Doe\", " +
                "  \"address\": { " +
                "    \"street\": \"123 Main St\", " +
                "    \"city\": \"Anytown\", " +
                "    \"state\": \"CA\", " +
                "    \"zip\": \"12345\" " +
                "  } " +
                "} }";

        String result = addressMapping.executeToJson(jsonInput);
        System.out.println("\nFlatten Structure Mapping Result:");
        System.out.println(result);
    }

    public static void nestFieldsMapping() {
        // Group flat fields into a nested structure
        Mapping productMapping = Platymap.flow("order")
                .withFormat(Format.JSON)
                .to("invoice")
                .withFormat(Format.JSON)
                .nest("order.item_*").asCollection("lineItems").to("invoice")
                .map("order.orderNumber").to("invoice.reference").end()
                .map("order.orderDate").to("invoice.date").end()
                .build();

        String jsonInput = "{ \"order\": { " +
                "  \"orderNumber\": \"ORD-12345\", " +
                "  \"orderDate\": \"2023-04-15\", " +
                "  \"item_1\": \"Widget\", " +
                "  \"item_2\": \"Gadget\", " +
                "  \"item_3\": \"Doodad\", " +
                "  \"item_price_1\": 10.99, " +
                "  \"item_price_2\": 24.99, " +
                "  \"item_price_3\": 5.99 " +
                "} }";

        String result = productMapping.executeToJson(jsonInput);
        System.out.println("\nNest Fields Mapping Result:");
        System.out.println(result);
    }
    public static void transformationMapping() {
        // Map fields with exclusions and transformations
        Mapping userMapping = Platymap.flow("user")
                .withFormat(Format.JSON)
                .to("profile")
                .withFormat(Format.JSON)
                .mapAll("user.*")
                .excluding("user.password", "user.ssn", "user.creditCard")
                .transformEach((key, value) -> {
                    // Uppercase all string values
                    if (value instanceof String) {
                        return ((String) value).toUpperCase();
                    } else if (value instanceof DataNode.StringValue) {
                        return new DataNode.StringValue(((DataNode.StringValue) value).getValue().toUpperCase());
                    }
                    return value;
                })
                .to("profile.userInfo")
                .end()
                .build();

        String jsonInput = "{ \"user\": { " +
                "  \"username\": \"jdoe\", " +
                "  \"firstName\": \"John\", " +
                "  \"lastName\": \"Doe\", " +
                "  \"email\": \"john@example.com\", " +
                "  \"password\": \"secret123\", " +
                "  \"ssn\": \"123-45-6789\", " +
                "  \"age\": 30, " +
                "  \"creditCard\": \"4111-1111-1111-1111\" " +
                "} }";

        String result = userMapping.executeToJson(jsonInput);
        System.out.println("\nTransformation Mapping Result:");
        System.out.println(result);
    }
}