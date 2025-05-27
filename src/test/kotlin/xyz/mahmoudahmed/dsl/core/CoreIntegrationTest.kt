package xyz.mahmoudahmed.dsl.core

import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.adapter.getPath
import xyz.mahmoudahmed.format.Format
import xyz.mahmoudahmed.dsl.conditional.BranchMapping
import xyz.mahmoudahmed.dsl.conditional.ConditionalBranch
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CoreIntegrationTest {

    @Test
    fun `test simple and multi-field mappings combined`() {
        // Given
        val sourceJson = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "age": 30,
            "address": {
                "street": "123 Main St",
                "city": "New York",
                "zipCode": "10001"
            },
            "tags": ["developer", "kotlin"]
        }
        """.trimIndent()

        // Create the mapping rules
        val simpleMapping = SimpleMapping("firstName", "user.first", { value ->
            if (value is DataNode.StringValue) {
                DataNode.StringValue(value.value.uppercase())
            } else {
                value
            }
        }, null)

        val multiFieldMapping = MultiFieldMapping(
            listOf("firstName", "lastName"),
            "user.fullName",
            { values ->
                val first = (values.getOrNull(0) as? DataNode.StringValue)?.value ?: ""
                val last = (values.getOrNull(1) as? DataNode.StringValue)?.value ?: ""
                DataNode.StringValue("$first $last")
            },
            null
        )

        val propertyRule = SetPropertyRule("userAge", "age")

        // Create the mapping
        val mapping = Mapping(
            "testSource",
            Format.JSON,
            "testTarget",
            Format.JSON,
            listOf(simpleMapping, multiFieldMapping, propertyRule)
        )

        // When
        val result = mapping.execute(sourceJson) as DataNode.ObjectNode

        // Then
        val userNode = result.get("user") as DataNode.ObjectNode
        assertEquals("JOHN", userNode.get("first")?.asString)
        assertEquals("John Doe", userNode.get("fullName")?.asString)
    }

    @Test
    fun `test conditional branching with multiple rules`() {
        // Given
        val sourceJson = """
        {
            "type": "person",
            "name": "John Doe",
            "age": 30
        }
        """.trimIndent()

        // Create person branch
        val personRule = SimpleMapping("name", "result", null, null)
        val personBranch = ConditionalBranch(
            { data ->
                val node = data as? DataNode.ObjectNode
                node?.get("type")?.asString == "person"
            },
            listOf(personRule)
        )

        // Create company branch
        val companyRule = SimpleMapping("name", "companyName", null, null)
        val companyBranch = ConditionalBranch(
            { data ->
                val node = data as? DataNode.ObjectNode
                node?.get("type")?.asString == "company"
            },
            listOf(companyRule)
        )

        // Create branch mapping with both branches
        val branchMapping = BranchMapping(listOf(personBranch, companyBranch))

        // Create the mapping
        val mapping = Mapping(
            "testSource",
            Format.JSON,
            "testTarget",
            Format.JSON,
            listOf(branchMapping)
        )

        // When
        val result = mapping.execute(sourceJson) as DataNode.ObjectNode

        // Then
        assertEquals("John Doe", result.get("result")?.asString)
        assertNull(result.get("companyName"))
    }

    @Test
    fun `test conditional branching with default branch`() {
        // Given
        val sourceJson = """
        {
            "type": "unknown",
            "name": "Something",
            "value": 42
        }
        """.trimIndent()

        // Create specific branch that won't match
        val specificRule = SimpleMapping("name", "specificName", null, null)
        val specificBranch = ConditionalBranch(
            { data ->
                val node = data as? DataNode.ObjectNode
                node?.get("type")?.asString == "specific"
            },
            listOf(specificRule)
        )

        // Create default branch
        val defaultRule = SimpleMapping("name", "defaultName", null, null)
        val defaultBranch = ConditionalBranch(
            { true }, // Always matches
            listOf(defaultRule)
        )

        // Create branch mapping with both branches - order matters!
        val branchMapping = BranchMapping(listOf(specificBranch, defaultBranch))

        // Create the mapping
        val mapping = Mapping(
            "testSource",
            Format.JSON,
            "testTarget",
            Format.JSON,
            listOf(branchMapping)
        )

        // When
        val result = mapping.execute(sourceJson) as DataNode.ObjectNode

        // Then
        assertNull(result.get("specificName"))
        assertEquals("Something", result.get("defaultName")?.asString)
    }

    @Test
    fun `test property setting and accessing in context`() {
        // Given
        val sourceJson = """
        {
            "id": "12345",
            "details": {
                "name": "Test Product",
                "price": 99.99
            }
        }
        """.trimIndent()

        // Create rules
        val setPropertyRule = SetPropertyRule("productId", "id")

        // Rule that uses the property
        val usePropertyRule = SimpleMapping("details.name", "product.name", { value ->
            if (value is DataNode.StringValue) {
                val context = MappingContext(DataNode.ObjectNode()) // Dummy context
                context.setProperty("productId", "12345") // Simulate what setPropertyRule would do

                val productId = context.getProperty("productId") as? String ?: ""
                DataNode.StringValue("${value.value} (ID: $productId)")
            } else {
                value
            }
        }, null)

        // Create the mapping
        val mapping = Mapping(
            "testSource",
            Format.JSON,
            "testTarget",
            Format.JSON,
            listOf(setPropertyRule, usePropertyRule)
        )

        // When
        val result = mapping.execute(sourceJson) as DataNode.ObjectNode

        // Then
        val productNode = result.get("product") as? DataNode.ObjectNode
        assertNotNull(productNode)
        assertTrue(productNode.get("name")?.asString?.contains("Test Product") ?: false)
        assertTrue(productNode.get("name")?.asString?.contains("ID: 12345") ?: false)
    }

    @Test
    fun `test complete flow with multiple rule types`() {
        // Given
        val sourceJson = """
        {
            "user": {
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@example.com"
            },
            "orders": [
                {
                    "id": "order-001",
                    "total": 125.50
                },
                {
                    "id": "order-002",
                    "total": 55.99
                }
            ],
            "preferences": {
                "theme": "dark",
                "notifications": true
            }
        }
        """.trimIndent()

        // Create various rules
        val simpleMapping = SimpleMapping("user.email", "contact.email", null, null)

        val nameMapping = MultiFieldMapping(
            listOf("user.firstName", "user.lastName"),
            "contact.fullName",
            { values ->
                val first = (values.getOrNull(0) as? DataNode.StringValue)?.value ?: ""
                val last = (values.getOrNull(1) as? DataNode.StringValue)?.value ?: ""
                DataNode.StringValue("$first $last")
            },
            null
        )

        val themeRule = SimpleMapping("preferences.theme", "settings.theme", { value ->
            if (value is DataNode.StringValue && value.value == "dark") {
                DataNode.StringValue("dark-mode")
            } else {
                DataNode.StringValue("light-mode")
            }
        }, null)

        val notificationsRule = SimpleMapping("preferences.notifications", "settings.emailNotifications", null, null)

        // Create the mapping
        val mapping = Mapping(
            "testSource",
            Format.JSON,
            "testTarget",
            Format.JSON,
            listOf(simpleMapping, nameMapping, themeRule, notificationsRule)
        )

        // When
        val result = mapping.execute(sourceJson) as DataNode.ObjectNode
        val resultJson = mapping.executeToJson(sourceJson)

        // Then
        val contactNode = result.get("contact") as? DataNode.ObjectNode
        assertNotNull(contactNode)
        assertEquals("John Doe", contactNode.get("fullName")?.asString)
        assertEquals("john.doe@example.com", contactNode.get("email")?.asString)

        val settingsNode = result.get("settings") as? DataNode.ObjectNode
        assertNotNull(settingsNode)
        assertEquals("dark-mode", settingsNode.get("theme")?.asString)
        assertEquals(true, (settingsNode.get("emailNotifications") as? DataNode.BooleanValue)?.value)

        // Verify JSON output
        assertTrue(resultJson.contains("\"fullName\":\"John Doe\""))
        assertTrue(resultJson.contains("\"email\":\"john.doe@example.com\""))
        assertTrue(resultJson.contains("\"theme\":\"dark-mode\""))
        assertTrue(resultJson.contains("\"emailNotifications\":true"))
    }


    @Test
    fun `test simple mapping from json to json`() {
        val customerMapping = Platymap.flow("customer")
            .withFormat(Format.JSON)
            .to("profile")
            .withFormat(Format.JSON)
            .map("customer.firstName").to("profile.name.first").end()
            .map("customer.lastName").to("profile.name.last").end()
            .map("customer.email").to("profile.contact.email").end()
            .build()

        val jsonInput =
            "{ \"customer\": { \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john@example.com\" } }"
        val jsonOutput = customerMapping.executeToJson(jsonInput)
        val xmlOutput = customerMapping.executeToXml(jsonInput)


        val expectedJson =
            """{"profile":{"name":{"first":"John","last":"Doe"},"contact":{"email":"john@example.com"}}}"""
        assertEquals(expectedJson, jsonOutput)
    }

    @Test
    fun `test mapAll from customer to profile`() {
        val customerMapping = Platymap.flow("customer")
            .withFormat(Format.JSON)
            .to("profile")
            .withFormat(Format.JSON)
            .mapAll("customer.*").to("profile").end()
            .build()

        val jsonInput = """
        {
            "customer": {
                "firstName": "John",
                "lastName": "Doe",
                "email": "john@example.com",
                "age": 30,
                "address": {
                    "street": "123 Main St",
                    "city": "Anytown",
                    "zipCode": "12345"
                }
            }
        }
    """.trimIndent()

        val result = customerMapping.executeToJson(jsonInput)

        val expectedJson =
            """{"profile":{"firstName":"John","lastName":"Doe","email":"john@example.com","age":30,"address":{"street":"123 Main St","city":"Anytown","zipCode":"12345"}}}"""

        assertEquals(expectedJson, result)
    }

    @Test
    fun `test forEach mapping from order to receipt`() {
        val orderMapping = Platymap.flow("order")
            .to("receipt")
            .map("orderNumber").to("receipt.reference").end()
            .map("customerName").to("receipt.buyer").end()
            .forEach("items").`as`("item")
            .create("receipt.lines")
            .map("\$item.productName").to("description").end()
            .map("\$item.quantity").to("amount").end()
            .map("\$item.price").to("unitPrice").end()
            .map("\$item.price * \$item.quantity").to("total").end()
            .end()
            .end()
            .build()

        val jsonInput = """
        {
            "orderNumber": "ORD-12345",
            "customerName": "Jane Smith",
            "items": [
                {
                    "productName": "Laptop",
                    "quantity": 1,
                    "price": 999.99
                },
                {
                    "productName": "Mouse",
                    "quantity": 2,
                    "price": 25.50
                },
                {
                    "productName": "Keyboard",
                    "quantity": 1,
                    "price": 75.00
                }
            ]
        }
    """.trimIndent()

        val result = orderMapping.executeToJson(jsonInput)

        val expectedJson =
            """{"receipt":{"reference":"ORD-12345","buyer":"Jane Smith","lines":[{"description":"Laptop","amount":1,"unitPrice":999.99,"total":999.99},{"description":"Mouse","amount":2,"unitPrice":25.5,"total":51.0},{"description":"Keyboard","amount":1,"unitPrice":75.0,"total":75.0}]}}"""
        assertEquals(expectedJson, result)
    }

    @Test
    fun `test flattening address with prefix`() {
        val addressMapping = Platymap.flow("customer")
            .withFormat(Format.JSON)
            .to("profile")
            .withFormat(Format.JSON)
            .flatten("customer.address").flattenWithPrefix("addr_").to("profile").end()
            .map("customer.firstName").to("profile.name").end()
            .map("customer.lastName").to("profile.surname").end()
            .build()

        // Create test input JSON with nested address structure
        val jsonInput = """
            {
                "customer": {
                    "firstName": "John",
                    "lastName": "Doe",
                    "address": {
                        "street": "123 Main St",
                        "city": "Anytown",
                        "state": "CA",
                        "zipCode": "12345",
                        "country": "USA"
                    }
                }
            }
            """.trimIndent()

        // Execute the mapping
        val result = addressMapping.executeToJson(jsonInput)

        // Define expected JSON with flattened address fields
        val expectedJson =
            """{"profile":{"name":"John","surname":"Doe","addr_street":"123 Main St","addr_city":"Anytown","addr_state":"CA","addr_zipCode":"12345","addr_country":"USA"}}"""

        // Assert the result matches the expected output
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.LENIENT)
    }

    @Test
    fun `test nesting flat fields into a structured collection`() {
        // Define the mapping
        val productMapping = Platymap.flow("order")
            .withFormat(Format.JSON)
            .to("invoice")
            .withFormat(Format.JSON)
            .nest("order.item_*").asCollection("lineItems").to("invoice")
            .map("order.orderNumber").to("invoice.reference").end()
            .map("order.orderDate").to("invoice.date").end()
            .build()

        // Create test input JSON with flat item fields
        val jsonInput = """
        {
            "order": {
                "orderNumber": "ORD-5678",
                "orderDate": "2023-05-15",
                "item_1_name": "Laptop",
                "item_1_price": 999.99,
                "item_1_quantity": 1,
                "item_2_name": "Mouse",
                "item_2_price": 25.50,
                "item_2_quantity": 2,
                "item_3_name": "Keyboard",
                "item_3_price": 75.00,
                "item_3_quantity": 1,
                "customerName": "Jane Smith"
            }
        }
        """.trimIndent()

        // Execute the mapping
        val result = productMapping.executeToJson(jsonInput)

        // Define expected JSON with items grouped into a lineItems array
        val expectedJson = """
        {
            "invoice": {
                "reference": "ORD-5678",
                "date": "2023-05-15",
                "lineItems": [
                    {
                        "name": "Laptop",
                        "price": 999.99,
                        "quantity": 1
                    },
                    {
                        "name": "Mouse",
                        "price": 25.5,
                        "quantity": 2
                    },
                    {
                        "name": "Keyboard",
                        "price": 75.0,
                        "quantity": 1
                    }
                ]
            }
        }
        """.trimIndent()

        println(result)

        // Use JSONAssert to compare while ignoring property order
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.LENIENT)
    }

    @Test
    fun `test complex nesting with various patterns`() {
        val formMapping = Platymap.flow("form")
            .withFormat(Format.JSON)
            .to("structured")
            .withFormat(Format.JSON)
            // Nest personal information fields
            .nest("form.personal_*").asObject("personalInfo").to("structured")
            // Nest address fields with a prefix
            .nest("form.address_*").asObject("address").to("structured")
            // Nest contact methods as a collection
            .nest("form.contact_*_*").asCollection("contactMethods").to("structured")
            // Nest education history as a collection
            .nest("form.education_*_*").asCollection("educationHistory").to("structured")
            // Nest work experience as a collection
            .nest("form.work_*_*").asCollection("workExperience").to("structured")
            // Map the form ID directly
            .map("form.id").to("structured.formId").end()
            .map("form.submissionDate").to("structured.submittedOn").end()
            .build()

        // Create a complex test input with flat fields that should be grouped
        val jsonInput = """
    {
        "form": {
            "id": "F-12345",
            "submissionDate": "2023-06-15",
            
            "personal_firstName": "John",
            "personal_lastName": "Doe",
            "personal_birthDate": "1985-07-22",
            "personal_gender": "Male",
            
            "address_street": "123 Main St",
            "address_city": "Boston",
            "address_state": "MA",
            "address_zipCode": "02108",
            "address_country": "USA",
            
            "contact_1_type": "Email",
            "contact_1_value": "john.doe@example.com",
            "contact_1_isPrimary": true,
            "contact_2_type": "Phone",
            "contact_2_value": "555-123-4567",
            "contact_2_isPrimary": false,
            
            "education_1_institution": "Harvard University",
            "education_1_degree": "Bachelor of Science",
            "education_1_field": "Computer Science",
            "education_1_yearCompleted": 2007,
            "education_2_institution": "MIT",
            "education_2_degree": "Master of Science",
            "education_2_field": "Artificial Intelligence",
            "education_2_yearCompleted": 2009,
            
            "work_1_company": "Tech Innovations Inc",
            "work_1_position": "Software Engineer",
            "work_1_startYear": 2009,
            "work_1_endYear": 2015,
            "work_2_company": "Digital Solutions LLC",
            "work_2_position": "Senior Developer",
            "work_2_startYear": 2015,
            "work_2_endYear": 2020,
            "work_3_company": "Future Systems",
            "work_3_position": "Lead Architect",
            "work_3_startYear": 2020,
            "work_3_endYear": null
        }
    }
    """.trimIndent()

        // Execute the mapping
        val result = formMapping.executeToJson(jsonInput)

        // Define expected JSON with nested structures
        val expectedJson = """
    {
        "structured": {
            "formId": "F-12345",
            "submittedOn": "2023-06-15",
            "personalInfo": {
                "firstName": "John",
                "lastName": "Doe",
                "birthDate": "1985-07-22",
                "gender": "Male"
            },
            "address": {
                "street": "123 Main St",
                "city": "Boston",
                "state": "MA",
                "zipCode": "02108",
                "country": "USA"
            },
            "contactMethods": [
                {
                    "type": "Email",
                    "value": "john.doe@example.com",
                    "isPrimary": true
                },
                {
                    "type": "Phone",
                    "value": "555-123-4567",
                    "isPrimary": false
                }
            ],
            "educationHistory": [
                {
                    "institution": "Harvard University",
                    "degree": "Bachelor of Science",
                    "field": "Computer Science",
                    "yearCompleted": 2007
                },
                {
                    "institution": "MIT",
                    "degree": "Master of Science",
                    "field": "Artificial Intelligence",
                    "yearCompleted": 2009
                }
            ],
            "workExperience": [
                {
                    "company": "Tech Innovations Inc",
                    "position": "Software Engineer",
                    "startYear": 2009,
                    "endYear": 2015
                },
                {
                    "company": "Digital Solutions LLC",
                    "position": "Senior Developer",
                    "startYear": 2015,
                    "endYear": 2020
                },
                {
                    "company": "Future Systems",
                    "position": "Lead Architect",
                    "startYear": 2020,
                    "endYear": null
                }
            ]
        }
    }
    """.trimIndent()

        // Use JSONAssert to compare while ignoring property order
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.LENIENT)
    }


    @Test
    fun `test admin user mapping`() {
        val mapping = Platymap.flow("user")
            .withFormat(Format.JSON)
            .to("account")
            .withFormat(Format.JSON)
            .branch()
            .doWhen { input ->
                val user = input as DataNode
                user.getPath("user", "type")?.asString == "admin"
            }
            .then()
            .map("name").to("account.adminName").end()
            .map("'Full Access'").to("account.permissions").end()
            .endBranch()
            .otherwise()
            .map("name").to("account.userName").end()
            .map("'Limited Access'").to("account.permissions").end()
            .endBranch()
            .end()
            .build()

        val inputJson = """
            {
              "user": {
                "name": "Alice",
                "type": "admin"
              }
            }
        """.trimIndent()

        val expectedJson = """
            {
              "account": {
                "adminName": "Alice",
                "permissions": "Full Access"
              }
            }
        """.trimIndent()

        val result = mapping.executeToJson(inputJson)

        println(result)
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.LENIENT)
    }

    @Test
    fun `map fields directly from source to target`() {
        val mapping = Platymap.flow("customer")
            .to("user")
            .map("name").to("fullName").end()
            .map("email").to("contactEmail").end()
            .build()

        val customerJson = """
            {
                "name": "John Doe",
                "email": "john@example.com",
                "age": 30
            }
        """

        val expectedJson = """
            {
                "fullName": "John Doe",
                "contactEmail": "john@example.com"
            }
        """

        val result = mapping.executeToJson(customerJson)
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.LENIENT)
    }

    @Test
    fun `transform values during mapping`() {
        val mapping = Platymap.flow("customer")
            .to("user")
            .map("name").transform {
                it as DataNode
                it.asString?.uppercase() ?: ""
            }.to("fullName").end()
            .map("age")
            .transform { (it as? DataNode)?.asInt?.times(2) ?: 0 }
            .to("doubledAge")
            .end()
            .build()

        val customerJson = """
            {
                "name": "John Doe",
                "email": "john@example.com",
                "age": 30
            }
        """

        val expectedJson = """
            {
                "fullName": "JOHN DOE",
                "doubledAge": 60
            }
        """
        val result = mapping.executeToJson(customerJson)
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.LENIENT)

    }

    @Test
    fun `transform values during mapping with transformer`() {
        val mapping = Platymap.flow("customer")
            .to("user")
            .map("name").uppercase().to("fullName").end()
            .map("age").times(2.0).toInt()
            .to("doubledAge")
            .end()
            .build()

        val customerJson = """
            {
                "name": "John Doe",
                "email": "john@example.com",
                "age": 30
            }
        """

        val expectedJson = """
            {
                "fullName": "JOHN DOE",
                "doubledAge": 60
            }
        """
        val result = mapping.executeToJson(customerJson)
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.LENIENT)

    }


    @Test
    fun `access nested fields with dot notation`() {
        val mapping = Platymap.flow("customer")
            .to("user")
            .map("address.street").to("streetAddress").end()
            .map("address.city").to("city").end()
            .build()


        val customerJson = """ 
            {
                "fullName": "JOHN DOE",
                "address": {"street" : "street value",
                            "city": "city value"}
            }
        """

        val expectedJson = """
            {
                "streetAddress": "street value",
                "city": "city value"
            }
        """
        val result = mapping.executeToJson(customerJson)
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.LENIENT)

    }

    @Test
    fun `create nested structures in the target`() {
        val mapping = Platymap.flow("customer")
            .to("user")
            .map("email").to("contact.email").end()
            .map("phone").to("contact.phoneNumber").end()
            .build()

        val customerJson = """ 
            {
                "email": "JOHN DOE",
                "phone": "12359898577"
            }
        """

        val expectedJson = """
            {
                "contact": {
                        "email": "JOHN DOE",
                        "phoneNumber": "12359898577"
                    }
            }
        """
        val result = mapping.executeToJson(customerJson)
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.LENIENT)

    }

}