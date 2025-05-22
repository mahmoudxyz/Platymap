package xyz.mahmoudahmed.dsl.core

/**
 * Exception thrown when an error occurs during mapping execution.
 */
class MappingExecutionException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String, path: String) : super("$message at path: $path")
}
