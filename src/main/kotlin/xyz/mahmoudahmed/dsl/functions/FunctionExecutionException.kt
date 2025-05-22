package xyz.mahmoudahmed.dsl.functions

/**
 * Exception thrown when a function execution fails.
 */
class FunctionExecutionException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
