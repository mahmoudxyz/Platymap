import xyz.mahmoudahmed.adapter.DataNode;
import dsl.FunctionRegistry;
import dsl.Mapping;
import dsl.Platymap;


public class Main {

    public static void main(String[] args) {
        // Example 1: Basic Mapping
        basicMapping();

        // Example 2: Collection Mapping
        collectionMapping();

        // Example 3: Conditional Mapping
        conditionalMapping();

        // Example 4: Function Mapping
        functionMapping();
    }

    public static void basicMapping() {
        Mapping customerMapping = Platymap.flow("customer")
                .to("profile")
                .map("firstName").to("profile.name.first").end()
                .map("lastName").to("profile.name.last").end()
                .map("email").to("profile.contact.email").end()
                .map("phone").to("profile.contact.phone").end()
                .build();

        String jsonInput = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", " +
                "\"email\": \"john@example.com\", \"phone\": \"555-1234\" }";

        Object result = customerMapping.executeToXml(jsonInput);
        System.out.println("Basic Mapping Result: " + result);
    }

    public static void collectionMapping() {
        Mapping orderMapping = Platymap.flow("order")
                .to("receipt")
                .map("orderNumber").to("receipt.reference").end()
                .map("customerName").to("receipt.buyer").end()
                .forEach("items").as("item")
                .create("receipt.lines")
                .map("item.productName").to("description").end()
                .map("item.quantity").to("amount").end()
                .map("item.price").to("unitPrice").end()
                .map("item.price * item.quantity").transform(obj -> {
                    if (obj instanceof DataNode.StringValue) {
                        return new DataNode.StringValue("Calculated total");
                    } else if (obj instanceof String) {
                        return new DataNode.StringValue("Calculated total");
                    }
                    return obj;
                }).to("total").end()
                .end()
                .end()
                .build();

        String jsonInput = "{ \"orderNumber\": \"ORD-123\", \"customerName\": \"John Doe\", " +
                "\"items\": [" +
                "  { \"productName\": \"Widget\", \"quantity\": 2, \"price\": 10.99 }," +
                "  { \"productName\": \"Gadget\", \"quantity\": 1, \"price\": 29.99 }" +
                "]}";

        Object result = orderMapping.executeToJson(jsonInput);
        System.out.println("Collection Mapping Result: " + result);
    }

    public static void conditionalMapping() {
        Mapping userMapping = Platymap.flow("user")
                .to("account")
                .branch()
                .when(user -> {
                    DataNode dataNode = (DataNode) user;
                    DataNode.ObjectNode objNode = dataNode.getAsObject();
                    DataNode typeNode = objNode.get("type");
                    return typeNode != null &&
                            "admin".equals(typeNode.getAsString());
                })
                .then()
                .map("name").to("account.adminName").end()
                .map("'Full Access'").to("account.permissions").end()
                .endBranch()
                .when(user -> {
                    DataNode dataNode = (DataNode) user;
                    DataNode.ObjectNode objNode = dataNode.getAsObject();
                    DataNode typeNode = objNode.get("type");
                    return typeNode != null &&
                            "regular".equals(typeNode.getAsString());
                })
                .then()
                .map("name").to("account.userName").end()
                .map("'Limited Access'").to("account.permissions").end()
                .endBranch()
                .otherwise()
                .map("name").to("account.guestName").end()
                .map("'Read Only'").to("account.permissions").end()
                .endBranch()
                .end()
                .build();

        String adminJson = "{ \"name\": \"Admin User\", \"type\": \"admin\" }";
        String regularJson = "{ \"name\": \"Regular User\", \"type\": \"regular\" }";
        String guestJson = "{ \"name\": \"Guest User\", \"type\": \"guest\" }";

        System.out.println("Admin Mapping: " + userMapping.executeToJson(adminJson));
        System.out.println("Regular Mapping: " + userMapping.executeToJson(regularJson));
        System.out.println("Guest Mapping: " + userMapping.executeToJson(guestJson));
    }

    public static void functionMapping() {
        // Define a custom function that correctly handles DataNode inputs
        Platymap.function("formatFullName")
                .with("firstName", "lastName")
                .body(args -> {
                    // Handle DataNode correctly
                    String firstName;
                    if (args[0] instanceof DataNode) {
                        firstName = ((DataNode) args[0]).getAsString();
                    } else {
                        firstName = String.valueOf(args[0]);
                    }

                    String lastName;
                    if (args[1] instanceof DataNode) {
                        lastName = ((DataNode) args[1]).getAsString();
                    } else {
                        lastName = String.valueOf(args[1]);
                    }

                    return new DataNode.StringValue(lastName + ", " + firstName);
                })
                .build();

        Mapping nameMapping = Platymap.flow("person")
                .to("contact")
                .map("firstName").transform(firstNameObj -> {
                    // Get the actual string value from DataNode
                    String firstName;
                    if (firstNameObj instanceof DataNode) {
                        firstName = ((DataNode) firstNameObj).getAsString();
                    } else {
                        firstName = String.valueOf(firstNameObj);
                    }

                    // Parse the JSON for lastName
                    DataNode lastNameNode = Platymap.getAdapterService()
                            .parseData("{\"lastName\":\"Doe\"}");
                    String lastName = lastNameNode.getAsObject().get("lastName").getAsString();

                    // Call our function with string values
                    return FunctionRegistry.call("formatFullName",
                            new DataNode.StringValue(firstName),
                            new DataNode.StringValue(lastName));
                })
                .to("contact.formattedName").end()
                .map("email").transform(emailObj -> {
                    // Get the actual string value and uppercase it
                    String email;
                    if (emailObj instanceof DataNode) {
                        email = ((DataNode) emailObj).getAsString();
                    } else {
                        email = String.valueOf(emailObj);
                    }
                    return new DataNode.StringValue(email.toUpperCase());
                })
                .to("contact.EMAIL").end()
                .build();

        String jsonInput = "{ \"firstName\": \"John\", \"email\": \"john@example.com\" }";
        Object result = nameMapping.execute(jsonInput);
        System.out.println("Function Mapping Result: " + result);
    }
}