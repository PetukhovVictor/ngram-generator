package org.jetbrains.ngramgenerator.io

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

class JsonFilesReader<T>(
        private val dirPath: String,
        private val filesExt: String,
        private val entityType: TypeReference<*>) {
    private fun readFile(file: File): ArrayList<T> {
        return jacksonObjectMapper().readValue(file.readText(), entityType)
    }

    private fun walkDirectory(callback: (T, File) -> Unit) {
        val dir = File(dirPath)
        dir.walkTopDown().forEach {
            if (it.isFile && it.extension == filesExt) {
                callback(readFile(it)[0], it)
            }
        }
    }

    fun run(callback: (T, File) -> Unit) {
        walkDirectory { content: T, file: File -> callback(content, file) }
    }
}