package xyz.mahmoudahmed.logging

class ConsoleLogDestination : LogDestination {
    override fun log(formattedMessage: String) {
        println(formattedMessage)
    }
}