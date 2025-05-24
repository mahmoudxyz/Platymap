package xyz.mahmoudahmed.logging

import java.io.File

class FileLogDestination(private val filePath: String) : LogDestination {
    override fun log(formattedMessage: String) {
        File(filePath).appendText("$formattedMessage\n")
    }
}