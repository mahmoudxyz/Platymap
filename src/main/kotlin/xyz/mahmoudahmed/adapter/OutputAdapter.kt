package xyz.mahmoudahmed.adapter

import xyz.mahmoudahmed.format.FormatType
import java.io.OutputStream
import java.io.Writer

interface OutputAdapter {
    fun canHandle(formatType: FormatType): Boolean
    fun serialize(node: DataNode): String
    fun serialize(node: DataNode, writer: Writer)
    fun serialize(node: DataNode, outputStream: OutputStream)
}