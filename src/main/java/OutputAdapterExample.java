import dsl.Format;
import dsl.Mapping;
import dsl.Platymap;

public class OutputAdapterExample {
    public static void main(String[] args) {
        // Create a mapping
        Mapping customerMapping = Platymap.flow("customer")
                .to("profile")
                .map("firstName").to("profile.name.first").end()
                .map("lastName").to("profile.name.last").end()
                .map("email").to("profile.contact.email").end()
                .map("phone").to("profile.contact.phone").end()
                .build();

        String jsonInput = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", " +
                "\"email\": \"john@example.com\", \"phone\": \"555-1234\" }";

        // Output as JSON
        String jsonOutput = customerMapping.executeToJson(jsonInput);
        System.out.println("JSON Output:");
        System.out.println(jsonOutput);

        // Output as XML
        String xmlOutput = customerMapping.executeToXml(jsonInput);
        System.out.println("\nXML Output:");
        System.out.println(xmlOutput);

        // Output using specified format
        String yamlOutput = customerMapping.executeToJson(jsonInput);
        System.out.println("\nJSON Output:");
        System.out.println(yamlOutput);
    }
}