package xyz.mahmoudahmed.adapter

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.*
import xyz.mahmoudahmed.format.FormatDetectionService
import xyz.mahmoudahmed.format.FormatType

class InputAdapterServiceTest {

    private lateinit var mockDetectionService: FormatDetectionService
    private lateinit var mockJsonAdapter: InputAdapter
    private lateinit var mockXmlAdapter: InputAdapter
    private lateinit var service: InputAdapterService

    @BeforeEach
    fun setup() {
        // Create mock format detection service
        mockDetectionService = mock(FormatDetectionService::class.java)

        // Create mock adapters
        mockJsonAdapter = mock(InputAdapter::class.java)
        `when`(mockJsonAdapter.canHandle(FormatType.JSON)).thenReturn(true)

        mockXmlAdapter = mock(InputAdapter::class.java)
        `when`(mockXmlAdapter.canHandle(FormatType.XML)).thenReturn(true)

        // Create service with custom registry and injected mocks
        val registry = InputAdapterRegistry()
        registry.register(mockJsonAdapter)
        registry.register(mockXmlAdapter)

        // Use reflection to inject mock detection service
        service = InputAdapterService()
        val field = InputAdapterService::class.java.getDeclaredField("formatDetectionService")
        field.isAccessible = true
        field.set(service, mockDetectionService)

        // Inject mock registry
        val registryField = InputAdapterService::class.java.getDeclaredField("adapterRegistry")
        registryField.isAccessible = true
        registryField.set(service, registry)
    }

    @Test
    fun `test parseData with byte array`() {
        // Setup test data
        val testData = "test data".toByteArray()
        val expectedResult = DataNode.StringValue("result")

        // Configure mocks
        `when`(mockDetectionService.detectFormat(testData)).thenReturn(FormatType.JSON)
        `when`(mockJsonAdapter.parse(testData)).thenReturn(expectedResult)

        // Execute test
        val result = service.parseData(testData)

        // Verify results
        assertSame(expectedResult, result)
        verify(mockDetectionService).detectFormat(testData)
        verify(mockJsonAdapter).parse(testData)
    }


}