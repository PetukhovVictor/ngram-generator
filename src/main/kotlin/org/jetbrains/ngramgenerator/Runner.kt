package org.jetbrains.ngramgenerator

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.ngramgenerator.generating.Grams
import org.jetbrains.ngramgenerator.generating.NgramGenerator
import org.jetbrains.ngramgenerator.helpers.TimeLogger
import org.jetbrains.ngramgenerator.io.JsonFilesReader
import org.jetbrains.ngramgenerator.structures.CstNode
import java.io.File

class Runner {
    companion object {
        fun run(sourcesPath: String, factorizedSourcesPath: String, allNgramsFilePath: String) {
            val ngramGenerator = NgramGenerator(d=3)
            val cstNodeReference = object: TypeReference<ArrayList<CstNode>>() {}
            val timeLogger = TimeLogger(task_name = "N-gram extraction")
            val mapper = ObjectMapper()

            JsonFilesReader<CstNode>(sourcesPath, "json", cstNodeReference).run { content: CstNode, file: File ->
                val grams: Grams = ngramGenerator.generate(content)
                val relativePath = file.relativeTo(File(sourcesPath))
                val outputPath = File("$factorizedSourcesPath/$relativePath")

                File("$factorizedSourcesPath/${relativePath.parent ?: ""}").mkdirs()
                outputPath.writeText(mapper.writeValueAsString(grams))

                println("$file: ${grams.size} n-grams extracted")
            }

            val writeTimeLogger = TimeLogger(task_name = "N-grams write")
            File(allNgramsFilePath).writeText(mapper.writeValueAsString(ngramGenerator.allNgrams))
            writeTimeLogger.finish()

            timeLogger.finish(fullFinish = true)
            println("${ngramGenerator.allNgrams.size} n-grams extracted")
        }
    }
}