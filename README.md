# Platymap

![img_3.png](img_3.png)

> **⚠️ WARNING: This project is in the very beginning stages as experimental. APIs may change significantly, and it is not yet ready for production use.**

Platymap is a high-performance, open-source data mapper library written in Kotlin/java, designed to be fully extensible and usable across various ecosystems through its flexible APIs. Inspired by the unique adaptability of the platypus, Platymap seamlessly transforms data between most common formats with powerful transformation capabilities along the way. Its modular, plugin-based architecture ensures effortless extension, deep integration, and smooth interoperability with any tech stack.

## Key Features

🦆 **Format Agnostic**: JSON, XML, CSV, YAML, and more.

⚡ **High Performance**: Stream-based processing to handle large datasets efficiently.

🔄 **Two-Way Mapping**: Configure bidirectional mappings easily.

🔍 **Schema Inference**: Automatically detects schemas from sample data.

✨ **Data Transformation**: Apply powerful transformations on-the-fly.

🔄 **Conditional Mapping**: Map fields based on conditional logic.

🔗 **Extensible Adapters**: Plugin support for custom formats and transformations.

🐛 **Rich Validation & Error Handling**: Detailed validation before processing.

## Getting Started

### Installation

```xml
<!-- Maven -->

```

```kotlin
// Gradle
```

### Basic Usage

```java
// Create a simple mapping from customer to profile
Mapping customerMapping = Platymap.flow("customer")
        .withFormat(Format.JSON)
        .to("profile")
        .withFormat(Format.JSON)
        .map("customer.firstName").to("profile.name.first").end()
        .map("customer.lastName").to("profile.name.last").end()
        .map("customer.email").to("profile.contact.email").end()
        .build();

// Execute the mapping on input data
String jsonInput = "{ \"customer\": { \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john@example.com\" } }";
String result = customerMapping.executeToJson(jsonInput);
```

## Examples

### Wildcard Mapping

Map all fields at once from one structure to another:

```java
Mapping customerMapping = Platymap.flow("customer")
        .withFormat(Format.JSON)
        .to("profile")
        .withFormat(Format.JSON)
        .mapAll("customer.*").to("profile").end()
        .build();
```

### Flatten Nested Structures

Transform nested objects into flat fields:

```java
Mapping addressMapping = Platymap.flow("customer")
        .withFormat(Format.JSON)
        .to("profile")
        .withFormat(Format.JSON)
        .flatten("customer.address").flattenWithPrefix("addr_").to("profile").end()
        .map("customer.firstName").to("profile.name").end()
        .map("customer.lastName").to("profile.surname").end()
        .build();
```

### Nest Flat Fields

Group related flat fields into a structured object:

```java
Mapping productMapping = Platymap.flow("order")
        .withFormat(Format.JSON)
        .to("invoice")
        .withFormat(Format.JSON)
        .nest("order.item_*").asCollection("lineItems").to("invoice")
        .map("order.orderNumber").to("invoice.reference").end()
        .map("order.orderDate").to("invoice.date").end()
        .build();
```

### Collection Mapping

Process arrays and collections:

```java
Mapping orderMapping = Platymap.flow("order")
        .to("receipt")
        .map("orderNumber").to("receipt.reference").end()
        .map("customerName").to("receipt.buyer").end()
        .forEach("items").as("item")
        .create("receipt.lines")
        .map("item.productName").to("description").end()
        .map("item.quantity").to("amount").end()
        .map("item.price").to("unitPrice").end()
        .map("item.price * item.quantity").to("total").end()
        .end()
        .end()
        .build();
```

### Conditional Mapping

Apply different mapping logic based on conditions:

```java
Mapping userMapping = Platymap.flow("user")
        .to("account")
        .branch()
        .when(user -> {
            // Check if user is admin
            DataNode dataNode = (DataNode) user;
            return "admin".equals(dataNode.getAsObject().get("type").getAsString());
        })
        .then()
        .map("name").to("account.adminName").end()
        .map("'Full Access'").to("account.permissions").end()
        .endBranch()
        .otherwise()
        .map("name").to("account.userName").end()
        .map("'Limited Access'").to("account.permissions").end()
        .endBranch()
        .end()
        .build();
```

### Field Exclusion and Transformation

Exclude sensitive fields and transform data during mapping:

```java
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
            }
            return value;
        })
        .to("profile.userInfo")
        .end()
        .build();
```

## Architecture

![img.png](img.png)

Platymap follows a modular architecture with:

- **Core Engine**: Handles the mapping execution and transformation logic
- **Format Adapters**: Convert between different data formats
- **Function Registry**: Manages custom transformation functions
- **Validation Layer**: Ensures data integrity during mapping

## Roadmap

- Enhanced schema validation
- Performance optimizations for large datasets
- Additional format adapters
- Visual mapping designer
- Cloud integration for mapping as a service

## Contributing

We welcome contributions! Please feel free to submit a Pull Request.

## License

Platymap is released under the MIT License.