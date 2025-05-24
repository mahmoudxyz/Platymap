package xyz.mahmoudahmed.logging;

interface LogDestination {
    fun log(formattedMessage: String)
}