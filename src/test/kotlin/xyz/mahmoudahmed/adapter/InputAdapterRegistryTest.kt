package xyz.mahmoudahmed.adapter


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.*
import xyz.mahmoudahmed.format.Format

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
        `when`(jsonAdapter.canHandle(Format.JSON)).thenReturn(true)

        val xmlAdapter = mock(InputAdapter::class.java)
        `when`(xmlAdapter.canHandle(Format.XML)).thenReturn(true)

        // Register adapters
        registry.register(jsonAdapter)
        registry.register(xmlAdapter)

        // Test getting the correct adapter
        val foundJsonAdapter = registry.getAdapter(Format.JSON)
        val foundXmlAdapter = registry.getAdapter(Format.XML)

        assertSame(jsonAdapter, foundJsonAdapter)
        assertSame(xmlAdapter, foundXmlAdapter)
    }

    @Test
    fun `test get adapter for unsupported format throws exception`() {
        // Create mock adapter that only supports JSON
        val adapter = mock(InputAdapter::class.java)
        `when`(adapter.canHandle(Format.JSON)).thenReturn(true)
        `when`(adapter.canHandle(Format.XML)).thenReturn(false)

        // Register adapter
        registry.register(adapter)

        // Test exception for unsupported format
        val exception = assertThrows(UnsupportedFormatException::class.java) {
            registry.getAdapter(Format.XML)
        }

        assertTrue(exception.message?.contains("No adapter found") ?: false)
    }

    @Test
    fun `test register multiple adapters for same format uses first registered`() {
        // Create two adapters that both support JSON
        val firstAdapter = mock(InputAdapter::class.java)
        `when`(firstAdapter.canHandle(Format.JSON)).thenReturn(true)

        val secondAdapter = mock(InputAdapter::class.java)
        `when`(secondAdapter.canHandle(Format.JSON)).thenReturn(true)

        // Register adapters in order
        registry.register(firstAdapter)
        registry.register(secondAdapter)

        // The first registered adapter should be returned
        val foundAdapter = registry.getAdapter(Format.JSON)
        assertSame(firstAdapter, foundAdapter)
    }
}