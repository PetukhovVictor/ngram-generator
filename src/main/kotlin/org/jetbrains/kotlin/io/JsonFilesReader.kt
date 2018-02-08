package org.jetbrains.kotlin.io

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

class JsonFilesReader<T>(private val dirPath: String, private val filesExt: String) {
    private fun readFile(file: File): T {
        val mapper = jacksonObjectMapper()

        return mapper.readValue(file.readText(), object: TypeReference<T>() {})
    }

    private fun walkDirectory(callback: (T) -> Unit) {
        File(dirPath).walkTopDown().forEach {
            if (it.isFile && it.extension == filesExt) {
                callback(readFile(it))
            }
        }
    }

    fun run(callback: (T) -> Unit) {
        walkDirectory { callback(it) }
    }
}