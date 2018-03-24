package org.jetbrains.ngramgenerator.io

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

class JsonFilesReader<T>(
        private val dirPath: String,
        private val filesExt: String,
        private val entityType: TypeReference<*>) {
    private fun readFile(file: File): T {
        return jacksonObjectMapper().readValue(file.readText(), entityType)
    }

    private fun walkDirectory(callback: (T, File) -> Unit) {
        val dir = File(dirPath)
        var current_file: File? = null
        var counter = 0

        try {
            dir.walkTopDown().forEach {
                if (it.isFile && it.name.endsWith(filesExt)) {
                    counter++
                    current_file = it
                    callback(readFile(it), it)
                }
            }
        } catch (e: MismatchedInputException) {
            println("ERROR ($counter): $e")
            println(current_file)
        }
    }

    fun run(callback: (T, File) -> Unit) {
        walkDirectory { content: T, file: File -> callback(content, file) }
    }
}