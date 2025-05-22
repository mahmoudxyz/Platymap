package xyz.mahmoudahmed.dsl.functions

/**
 * Exception thrown when a function is not found in the registry.
 */
class FunctionNotFoundException(name: String) :
    RuntimeException("Function not found: $name")