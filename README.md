# Platymap

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/kotlin-%230095D5.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)

**A high-performance, open-source data mapper library for seamless data transformation across formats**

Platymap is a powerful data transformation library written in Kotlin, designed to be fully extensible and usable across various ecosystems. Inspired by the unique adaptability of the platypus, Platymap seamlessly transforms data between the most common formats with powerful transformation capabilities along the way.

> ⚠️ **This project is experimental and in its very beginning stages.** APIs may change significantly, and it is not yet ready for production use.

## 🦆 Key Features

- **🔄 Format Agnostic**: JSON, XML, CSV, YAML, and more
- **⚡ High Performance**: Stream-based processing to handle large datasets efficiently
- **🔄 Two-Way Mapping**: Configure bidirectional mappings easily
- **🔍 Schema Inference**: Automatically detects schemas from sample data
- **✨ Data Transformation**: Over 70 built-in transformers for powerful on-the-fly transformations
- **🔄 Conditional Mapping**: Map fields based on conditional logic
- **🔗 Extensible Adapters**: Plugin support for custom formats and transformations
- **🐛 Rich Validation & Error Handling**: Detailed validation before processing

## 🚀 Quick Start

### Installation

#### Maven
```xml
<dependency>
    <groupId>com.platymap</groupId>
    <artifactId>platymap-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### Gradle
```kotlin
implementation("com.platymap:platymap-core:1.0.0-SNAPSHOT")
```

### Basic Usage

Transform data from one format to another with intuitive field mapping:

```kotlin
// Create a simple mapping from customer to profile
val customerMapping = Platymap.flow("customer")
    .withFormat(Format.JSON)
    .to("profile")
    .withFormat(Format.JSON)
    .map("customer.firstName").to("profile.name.first").end()
    .map("customer.lastName").to("profile.name.last").end()
    .map("customer.email").to("profile.contact.email").end()
    .build()

val jsonInput = """
    {
        "customer": {
            "firstName": "John",
            "lastName": "Doe",
            "email": "john@example.com"
        }
    }
"""

val jsonOutput = customerMapping.executeToJson(jsonInput)
val xmlOutput = customerMapping.executeToXml(jsonInput)
```

**Output:**
```json
{
  "profile": {
    "name": {
      "first": "John",
      "last": "Doe"
    },
    "contact": {
      "email": "john@example.com"
    }
  }
}
```

## 🎯 Common Use Cases

### Simple Field Mapping
```kotlin
val mapping = Platymap.flow("customer")
    .to("user")
    .map("name").to("fullName").end()
    .map("email").to("contactEmail").end()
    .build()
```

### Wildcard Mapping
Map all fields at once from one structure to another:
```kotlin
val customerMapping = Platymap.flow("customer")
    .to("profile")
    .mapAll("customer.*").to("profile").end()
    .build()
```

### Data Transformation
Apply powerful transformations during mapping:
```kotlin
val mapping = Platymap.flow("product")
    .to("displayProduct")
    .map("name").trim().capitalize().to("displayName").end()
    .map("price").roundToTwoDecimals().formatUSD().to("formattedPrice").end()
    .map("description").stripHtml().truncate().to("shortDescription").end()
    .build()
```

### Flatten Nested Structures
Transform nested objects into flat fields:
```kotlin
val addressMapping = Platymap.flow("customer")
    .to("profile")
    .flatten("customer.address").flattenWithPrefix("addr_").to("profile").end()
    .map("customer.firstName").to("profile.name").end()
    .build()
```

### Collection Processing
Process arrays and collections with forEach:
```kotlin
val orderMapping = Platymap.flow("order")
    .to("receipt")
    .map("orderNumber").to("receipt.reference").end()
    .forEach("items").`as`("item")
        .create("receipt.lines")
        .map("$item.productName").to("description").end()
        .map("$item.quantity").to("amount").end()
        .map("$item.price * $item.quantity").to("total").end()
        .end()
    .end()
    .build()
```

### Conditional Mapping
Apply different mapping logic based on conditions:
```kotlin
val mapping = Platymap.flow("user")
    .to("account")
    .branch()
        .when { source -> (source as? Map<*, *>)?.get("type") == "admin" }
        .then()
            .map("name").to("adminName").end()
            .map("'Full Access'").to("permissions").end()
        .endBranch()
        .otherwise()
            .map("name").to("userName").end()
            .map("'Limited Access'").to("permissions").end()
        .endBranch()
    .end()
    .build()
```

## 🔧 Advanced Features

### Built-in Transformations
Platymap includes over 70 built-in transformers covering:

- **String Transformations**: `uppercase()`, `lowercase()`, `trim()`, `truncate()`, `stripHtml()`, `mask()`, `slugify()`
- **Number Transformations**: `round()`, `formatUSD()`, `toPercentage()`, `increment()`
- **Date Transformations**: `formatDateLong()`, `addOneDay()`, `extractYear()`, `toIsoDate()`
- **Boolean Transformations**: `toYesNo()`, `toActiveInactive()`, `negate()`
- **Collection Transformations**: `join()`, `size()`, `first()`, `last()`, `sort()`
- **Type Conversions**: `toString()`, `toInt()`, `toBoolean()`, `parseJson()`

### Custom Transformers
Create your own transformers for specific needs:
```kotlin
class PhoneNumberFormatter : ValueTransformer {
    override fun transform(value: Any): Any {
        val phone = value.toString().replace(Regex("\\D"), "")
        return if (phone.length == 10) {
            "(${phone.substring(0, 3)}) ${phone.substring(3, 6)}-${phone.substring(6)}"
        } else phone
    }
}

TransformerRegistry.register("formatPhone", PhoneNumberFormatter())
```

### Type-Safe Mappings
For Java beans, use type-safe mappings:
```kotlin
data class Customer(val id: Long, val name: String, val email: String)
data class UserDTO(var userId: Long = 0, var fullName: String = "", var contactEmail: String = "")

val mapping = Platymap
    .flow(Customer::class.java)
    .to(UserDTO::class.java)
    .map { it.id }.to { dto, value -> dto.userId = value }
    .map { it.name }.to { dto, value -> dto.fullName = value }
    .map { it.email }.to { dto, value -> dto.contactEmail = value }
    .build()
```

## 🏗️ Architecture

Platymap follows a modular architecture with:

- **Core Engine**: Handles the mapping execution and transformation logic
- **Format Adapters**: Convert between different data formats
- **Function Registry**: Manages custom transformation functions
- **Validation Layer**: Ensures data integrity during mapping

## 🗺️ Roadmap

- [ ] Enhanced schema validation
- [ ] Performance optimizations for large datasets
- [ ] Additional format adapters
- [ ] Visual mapping designer
- [ ] Cloud integration for mapping as a service

## 📖 Documentation

For comprehensive documentation, visit our [documentation site](https://platymap.dev) which includes:

- **Getting Started Guide**: Learn the basics of Platymap
- **Advanced Mapping**: Complex transformations and conditional logic
- **Transformation System**: Complete guide to all built-in transformers
- **Validation DSL**: Data validation before processing
- **API Reference**: Complete API documentation

## 🤝 Contributing

We welcome contributions! Please feel free to submit a Pull Request. See our [Contributing Guide](CONTRIBUTING.md) for details.

## 📄 License

Platymap is released under the [MIT License](LICENSE).

---

<div align="center">
  <p><strong>Platymap - Adapt. Transform. Map.</strong></p>
  <p>Like the platypus, uniquely versatile in any environment.</p>
</div>
