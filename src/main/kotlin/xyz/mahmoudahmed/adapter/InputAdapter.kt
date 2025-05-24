package xyz.mahmoudahmed.adapter

import xyz.mahmoudahmed.format.Format
import java.io.File
import java.io.InputStream


interface InputAdapter {
    fun canHandle(format: Format): Boolean
    fun parse(data: ByteArray): DataNode
    fun parse(content: String): DataNode
    fun parse(file: File): DataNode
    fun parse(inputStream: InputStream): DataNode
}
