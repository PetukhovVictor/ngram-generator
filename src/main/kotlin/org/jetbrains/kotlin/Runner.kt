package org.jetbrains.kotlin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.kotlin.generating.Grams
import org.jetbrains.kotlin.generating.NgramGenerator
import org.jetbrains.kotlin.helpers.TimeLogger
import org.jetbrains.kotlin.io.JsonFilesReader
import org.jetbrains.kotlin.structures.CstNode
import java.io.File

class Runner {
    companion object {
        fun run(sourcesPath: String, factorizedSourcesPath: String, allNgramsFilePath: String) {
            val ngramGenerator = NgramGenerator(d=3)
            val cstNodeReference = object: TypeReference<CstNode>() {}
            val mapper = ObjectMapper()
            val timeLogger = TimeLogger(task_name = "N-gram extraction")

            JsonFilesReader<CstNode>(sourcesPath, "json", cstNodeReference).run { content: CstNode, file: File ->
                val grams: Grams = ngramGenerator.generate(content)
                val relativePath = file.relativeTo(File(sourcesPath))
                val outputPath = File("$factorizedSourcesPath/$relativePath")

                File("$factorizedSourcesPath/${relativePath.parent ?: ""}").mkdirs()
                outputPath.writeText(mapper.writeValueAsString(grams))

                print("$file: ")
                println(grams.size)
            }

            File(allNgramsFilePath).writeText(mapper.writeValueAsString(ngramGenerator.allNgrams))

            timeLogger.finish(fullFinish = true)
            print("${ngramGenerator.allNgrams.size} n-grams extracted")
        }
    }
}