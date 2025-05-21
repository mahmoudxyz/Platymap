package xyz.mahmoudahmed.adapter


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.*
import xyz.mahmoudahmed.format.FormatType

class InputAdapterRegistryTest {

    private lateinit var registry: InputAdapterRegistry

    @BeforeEach
    fun setup() {
        registry = InputAdapterRegistry()
    }

    @Test
    fun `test get adapter for supported format`() {
        // Create mock adapters
        val jsonAdapter = mock(InputAdapter::class.java)
        `when`(jsonAdapter.canHandle(FormatType.JSON)).thenReturn(true)

        val xmlAdapter = mock(InputAdapter::class.java)
        `when`(xmlAdapter.canHandle(FormatType.XML)).thenReturn(true)

        // Register adapters
        registry.register(jsonAdapter)
        registry.register(xmlAdapter)

        // Test getting the correct adapter
        val foundJsonAdapter = registry.getAdapter(FormatType.JSON)
        val foundXmlAdapter = registry.getAdapter(FormatType.XML)

        assertSame(jsonAdapter, foundJsonAdapter)
        assertSame(xmlAdapter, foundXmlAdapter)
    }

    @Test
    fun `test get adapter for unsupported format throws exception`() {
        // Create mock adapter that only supports JSON
        val adapter = mock(InputAdapter::class.java)
        `when`(adapter.canHandle(FormatType.JSON)).thenReturn(true)
        `when`(adapter.canHandle(FormatType.XML)).thenReturn(false)

        // Register adapter
        registry.register(adapter)

        // Test exception for unsupported format
        val exception = assertThrows(UnsupportedFormatException::class.java) {
            registry.getAdapter(FormatType.XML)
        }

        assertTrue(exception.message?.contains("No adapter found") ?: false)
    }

    @Test
    fun `test register multiple adapters for same format uses first registered`() {
        // Create two adapters that both support JSON
        val firstAdapter = mock(InputAdapter::class.java)
        `when`(firstAdapter.canHandle(FormatType.JSON)).thenReturn(true)

        val secondAdapter = mock(InputAdapter::class.java)
        `when`(secondAdapter.canHandle(FormatType.JSON)).thenReturn(true)

        // Register adapters in order
        registry.register(firstAdapter)
        registry.register(secondAdapter)

        // The first registered adapter should be returned
        val foundAdapter = registry.getAdapter(FormatType.JSON)
        assertSame(firstAdapter, foundAdapter)
    }
}