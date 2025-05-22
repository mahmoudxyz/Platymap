import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.*
import xyz.mahmoudahmed.dsl.builders.*
import xyz.mahmoudahmed.dsl.bulk.*
import xyz.mahmoudahmed.dsl.collections.*
import xyz.mahmoudahmed.dsl.conditional.*
import xyz.mahmoudahmed.dsl.functions.*
import xyz.mahmoudahmed.dsl.structure.*
import xyz.mahmoudahmed.format.FormatType

/**
 * Comprehensive example of using the mapping DSL
 */
fun main() {
    // Example 1: Basic mapping with simple transformations
    val basicMapping = Platymap.flow("customer")
        .withFormat(FormatType.JSON)
        .to("user")
        .withFormat(FormatType.JSON)
        .map("name").transform { it.toString().uppercase() }.to("fullName").end()
        .map("email").to("contactEmail").end()
        .map("age").transform { (it as DataNode.NumberValue).asInt?.times(2) ?: 0  }.to("doubledAge").end()
        .build()

    val customerJson = """
        {
            "name": "John Doe",
            "email": "john@example.com",
            "age": 30,
            "address": {
                "street": "123 Main St",
                "city": "Anytown",
                "zip": "12345"
            }
        }
    """.trimIndent()

    val basicResult = basicMapping.executeToJson(customerJson)
    println("Basic Mapping Result:")
    println(basicResult)
    println()

    // Example 2: Conditional branching
    val conditionalMapping = Platymap.flow("customer")
        .withFormat(FormatType.JSON)
        .to("processedCustomer")
        .branch()
        .doWhen { source -> (source as DataNode).asObject?.get("age")?.let { (it as DataNode.NumberValue).value.toInt() >= 18 } ?: false }
        .then()
        .map("name").to("adultName").end()
        .map("status").transform { "adult" }.to("customerType").end()
        .endBranch()
        .otherwise()
        .map("name").to("minorName").end()
        .map("status").transform { "minor" }.to("customerType").end()
        .endBranch()
        .end()
        .map("email").to("contactInfo.email").end()
        .build()

    val customer1 = """
        { "name": "John Doe", "email": "john@example.com", "age": 30 }
    """.trimIndent()

    val customer2 = """
        { "name": "Jane Doe", "email": "jane@example.com", "age": 16 }
    """.trimIndent()

    println("Conditional Mapping Results:")
    println("Adult customer: ${conditionalMapping.executeToJson(customer1)}")
    println("Minor customer: ${conditionalMapping.executeToJson(customer2)}")
    println()

    // Example 3: Collection processing with forEach
//    val collectionMapping = Platymap.flow("order")
//        .withFormat(FormatType.JSON)
//        .to("processedOrder")
//        .map("orderId").to("id").end()
//        .map("customerName").transform { "Customer: $it" }.to("customer").end()
//        .forEach("items")
//        .`as`("item")
//        .create("processedItems")
//        .map("$item.name").transform { it.toString().uppercase() }.to("productName").end()
//        .map("$item.quantity").to("qty").end()
//        .map("$item.price").to("unitPrice")
//        .map("$item.quantity").transform { qty ->
//            val quantity = (qty as Number).toDouble()
//            val price = (context.getValueByPath("$item.price") as Number).toDouble()
//            quantity * price
//        }.to("totalPrice")
//        .end()
//        .end()
//        .map("total").to("orderTotal")
//        .build()
//
//    val orderJson = """
//        {
//            "orderId": "ORD-12345",
//            "customerName": "John Smith",
//            "items": [
//                { "name": "Laptop", "quantity": 1, "price": 1200.00 },
//                { "name": "Mouse", "quantity": 2, "price": 25.50 },
//                { "name": "Keyboard", "quantity": 1, "price": 85.75 }
//            ],
//            "total": 1336.75
//        }
//    """.trimIndent()
//
//    val collectionResult:String = collectionMapping.executeToJson(orderJson)
//    println("Collection Processing Result:")
//    println(collectionResult)
//    println()

    // Example 4: Bulk mapping and nesting
    val nestedMapping = Platymap.flow("flatCustomer")
        .withFormat(FormatType.JSON)
        .to("nestedCustomer")
        .mapAll("customer.*")
        .excluding("customer.address.*")
        .to("customerInfo")
        .end()
        .nest("customer.address.*")
        .asObject("addressDetails")
        .to("customerInfo")
        .map("metadata.createdAt").to("createdTimestamp").end()
        .build()

    val flatCustomerJson = """
        {
            "customer.id": 12345,
            "customer.name": "Alice Johnson",
            "customer.email": "alice@example.com",
            "customer.address.street": "456 Oak Lane",
            "customer.address.city": "Metropolis",
            "customer.address.zip": "54321",
            "metadata.createdAt": "2023-01-15T10:30:00Z"
        }
    """.trimIndent()

    val nestedResult = nestedMapping.executeToJson(flatCustomerJson)
    println("Nesting Result:")
    println(nestedResult)
    println()

    // Example 5: Custom functions and complex transformations
    // Example 5: Custom functions and complex transformations (continued)
    FunctionRegistry.register(
        MapFunction("formatCurrency", listOf("amount", "currency")) { args ->
            val amount = (args[0] as Number).toDouble()
            val currency = args[1]?.toString() ?: "USD"
            val symbol = when(currency) {
                "USD" -> "$"
                "EUR" -> "€"
                "GBP" -> "£"
                else -> currency
            }
            "$symbol${String.format("%.2f", amount)}"
        }
    )

    // Create a custom date formatter function
    FunctionRegistry.register(
        MapFunction("formatDate", listOf("date", "pattern")) { args ->
            val dateStr = args[0]?.toString() ?: ""
            val pattern = args[1]?.toString() ?: "yyyy-MM-dd"

            try {
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val outputFormat = java.text.SimpleDateFormat(pattern)
                val date = inputFormat.parse(dateStr)
                outputFormat.format(date)
            } catch (e: Exception) {
                throw FunctionExecutionException("Failed to format date: $dateStr with pattern: $pattern", e)
            }
        }
    )

//    val functionsMapping = Platymap.flow("product")
//        .withFormat(Format.JSON)
//        .to("formattedProduct")
//        .map("name").to("productName")
//        .map("price").transform { price ->
//            FunctionRegistry.call("formatCurrency", price, "USD")
//        }.to("formattedPrice")
//        .map("createdAt").transform { date ->
//            FunctionRegistry.call("formatDate", date, "MM/dd/yyyy")
//        }.to("releaseDate")
//        .map("categories").transform { categories ->
//            (categories as? List<*>)?.joinToString(", ") ?: ""
//        }.to("categoryList")
//        .build()
//
//    val productJson = """
//        {
//            "name": "Premium Headphones",
//            "price": 149.99,
//            "createdAt": "2023-06-15T08:30:00Z",
//            "categories": ["Electronics", "Audio", "Accessories"]
//        }
//    """.trimIndent()
//
//    val functionsResult = functionsMapping.executeToJson(productJson)
//    println("Custom Functions Result:")
//    println(functionsResult)
//    println()
//
//    // Example 6: Flattening nested structures
//    val flattenMapping = Platymap.flow("nestedOrder")
//        .withFormat(Format.JSON)
//        .to("flatOrder")
//        .map("id").to("orderId")
//        .map("customer.name").to("customerName")
//        .map("customer.email").to("customerEmail")
//        .flatten("customer.address")
//        .withPrefix("address_")
//        .to("shipping")
//        .end()
//        .forEach("items")
//        .`as`("item")
//        .create("lineItems")
//        .map("$item.name").to("productName")
//        .map("$item.quantity").to("quantity")
//        .map("$item.price").to("price")
//        .end()
//        .end()
//        .build()
//
//    val nestedOrderJson = """
//        {
//            "id": "ORD-98765",
//            "customer": {
//                "name": "Robert Brown",
//                "email": "robert@example.com",
//                "address": {
//                    "street": "789 Elm Avenue",
//                    "city": "Bigtown",
//                    "state": "CA",
//                    "zip": "90210",
//                    "country": "USA"
//                }
//            },
//            "items": [
//                { "name": "Smartphone", "quantity": 1, "price": 899.00 },
//                { "name": "Case", "quantity": 1, "price": 49.99 },
//                { "name": "Screen Protector", "quantity": 2, "price": 15.50 }
//            ]
//        }
//    """.trimIndent()
//
//    val flattenResult = flattenMapping.executeToJson(nestedOrderJson)
//    println("Flatten Structure Result:")
//    println(flattenResult)
//    println()
//
//    // Example 7: Combining multiple techniques with error handling
//    try {
//        val complexMapping = Platymap.flow("complexData")
//            .withFormat(Format.JSON)
//            .to("processedData")
//            // Basic mapping
//            .map("metadata.version").to("apiVersion")
//            .map("metadata.timestamp").transform { ts ->
//                try {
//                    FunctionRegistry.call("formatDate", ts, "yyyy-MM-dd")
//                } catch (e: Exception) {
//                    "Unknown Date"
//                }
//            }.to("processedDate")
//
//            // Conditional branching based on data type
//            .branch()
//            .when { source ->
//                (source as? DataNode)?.get("type")?.toString() == "order"
//            }
//            .then()
//            .map("payload.orderId").to("entityId")
//            .map("payload.total").transform { total ->
//                FunctionRegistry.call("formatCurrency", total, "USD")
//            }.to("amount")
//            .map("entityType").transform { "Order" }.to("entityType")
//            .endBranch()
//            .when { source ->
//                (source as? DataNode)?.get("type")?.toString() == "payment"
//            }
//            .then()
//            .map("payload.paymentId").to("entityId")
//            .map("payload.amount").transform { amount ->
//                FunctionRegistry.call("formatCurrency", amount, "USD")
//            }.to("amount")
//            .map("entityType").transform { "Payment" }.to("entityType")
//            .endBranch()
//            .otherwise()
//            .map("type").to("entityType")
//            .map("payload.id").to("entityId")
//            .map("payload.value").to("amount")
//            .endBranch()
//            .end()
//
//            // Collection handling
//            .forEach("payload.items")
//            .`as`("item")
//            .create("lineItems")
//            .map("$item.id").to("id")
//            .map("$item.name").to("description")
//            .map("$item.value").transform { value ->
//                if (value is Number) {
//                    FunctionRegistry.call("formatCurrency", value, "USD")
//                } else {
//                    value.toString()
//                }
//            }.to("formattedValue")
//
//            // Nested conditional logic
//            .branch()
//            .when { item ->
//                    val qty = (item as? DataNode)?.get("quantity")
//                qty != null && qty is DataNode.NumberValue && qty.value.toInt() > 1
//            }
//            .then()
//            .map("$item.quantity").to("qty")
//            .map("bulkDiscount").transform { "Yes" }.to("hasBulkDiscount")
//            .endBranch()
//            .otherwise()
//            .map("$item.quantity").to("qty")
//            .map("bulkDiscount").transform { "No" }.to("hasBulkDiscount")
//            .endBranch()
//            .end()
//            .end()
//            .end()
//
//            // Nesting flat fields
//            .nest("metadata.tags.*")
//            .asCollection("tags")
//            .to("metadata")
//
//            // Final transformations
//            .map("metadata.processed").transform { true }.to("isProcessed")
//            .map("metadata.processedAt").transform {
//                java.time.LocalDateTime.now().toString()
//            }.to("processTimestamp")
//            .build()
//
//        val complexJson = """
//            {
//                "type": "order",
//                "metadata": {
//                    "version": "2.0",
//                    "timestamp": "2023-08-12T14:45:30Z",
//                    "tags.0": "important",
//                    "tags.1": "retail",
//                    "tags.2": "priority"
//                },
//                "payload": {
//                    "orderId": "ORD-55555",
//                    "total": 1250.75,
//                    "items": [
//                        { "id": "PROD-1", "name": "High-End Monitor", "value": 899.99, "quantity": 1 },
//                        { "id": "PROD-2", "name": "USB Cable Pack", "value": 29.95, "quantity": 3 },
//                        { "id": "PROD-3", "name": "Wireless Mouse", "value": 45.50, "quantity": 2 }
//                    ]
//                }
//            }
//        """.trimIndent()
//
//        val complexResult = complexMapping.executeToJson(complexJson)
//        println("Complex Mapping Result:")
//        println(complexResult)
//    } catch (e: MappingExecutionException) {
//        println("Error during mapping: ${e.message}")
//        e.printStackTrace()
//    }
//
//    // Example 8: Using type-safe mapping for Java beans
//    data class Customer(
//        var id: Long = 0,
//        var name: String = "",
//        var email: String = "",
//        var address: Address? = null,
//        var orders: List<Order> = emptyList()
//    )
//
//    data class Address(
//        var street: String = "",
//        var city: String = "",
//        var zip: String = ""
//    )
//
//    data class Order(
//        var id: String = "",
//        var amount: Double = 0.0,
//        var date: String = ""
//    )
//
//    data class CustomerDTO(
//        var customerId: Long = 0,
//        var fullName: String = "",
//        var contactEmail: String = "",
//        var shippingAddress: String = "",
//        var orderCount: Int = 0,
//        var totalSpent: Double = 0.0
//    )
//
//    val typedMapping = Platymap
//        .flow(Customer::class.java)
//        .withFormat(FormatType.JAVA_BEAN)
//        .to(CustomerDTO::class.java)
//        .map { it.id }.to { dto, value -> dto.customerId = value }
//        .map { it.name }.to { dto, value -> dto.fullName = value }
//        .map { it.email }.to { dto, value -> dto.contactEmail = value }
//        .map { it.address }.transform { address ->
//            if (address != null) {
//                "${address.street}, ${address.city} ${address.zip}"
//            } else {
//                "No address provided"
//            }
//        }.to { dto, value -> dto.shippingAddress = value }
//        .map { it.orders }.transform { orders ->
//            orders.size
//        }.to { dto, value -> dto.orderCount = value }
//        .map { it.orders }.transform { orders ->
//            orders.sumOf { it.amount }
//        }.to { dto, value -> dto.totalSpent = value }
//        .build()
//
//    val customer = Customer(
//        id = 12345,
//        name = "Sarah Johnson",
//        email = "sarah@example.com",
//        address = Address(
//            street = "101 Pine Street",
//            city = "Springfield",
//            zip = "55555"
//        ),
//        orders = listOf(
//            Order("ORD-1", 125.50, "2023-01-15"),
//            Order("ORD-2", 89.99, "2023-03-22"),
//            Order("ORD-3", 299.95, "2023-07-08")
//        )
//    )
//
//    val customerDTO = typedMapping.execute(customer)
//    println("Type-Safe Bean Mapping Result:")
//    println("Customer ID: ${customerDTO.customerId}")
//    println("Name: ${customerDTO.fullName}")
//    println("Email: ${customerDTO.contactEmail}")
//    println("Address: ${customerDTO.shippingAddress}")
//    println("Order Count: ${customerDTO.orderCount}")
//    println("Total Spent: ${customerDTO.totalSpent}")
}
            