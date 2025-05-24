package xyz.mahmoudahmed.adapter

import xyz.mahmoudahmed.format.Format
import java.io.OutputStream
import java.io.Writer

interface OutputAdapter {
    fun canHandle(format: Format): Boolean
    fun serialize(node: DataNode): String
    fun serialize(node: DataNode, writer: Writer)
    fun serialize(node: DataNode, outputStream: OutputStream)
}