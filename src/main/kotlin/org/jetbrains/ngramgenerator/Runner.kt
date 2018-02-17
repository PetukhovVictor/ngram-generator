package org.jetbrains.ngramgenerator

import com.fasterxml.jackson.core.type.TypeReference
import org.jetbrains.ngramgenerator.generating.Grams
import org.jetbrains.ngramgenerator.generating.NgramGenerator
import org.jetbrains.ngramgenerator.helpers.TimeLogger
import org.jetbrains.ngramgenerator.io.FileWriter
import org.jetbrains.ngramgenerator.io.JsonFilesReader
import org.jetbrains.ngramgenerator.structures.CstNode
import java.io.File

class Runner {
    companion object {
        fun run(cstsPath: String, cstVectorsPath: String, allNgramsFilePath: String) {
            val ngramGenerator = NgramGenerator(d = 3)
            val cstNodeReference = object: TypeReference<ArrayList<CstNode>>() {}
            val timeLogger = TimeLogger(task_name = "N-gram extraction")

            JsonFilesReader<CstNode>(cstsPath, "json", cstNodeReference).run { content: CstNode, file: File ->
                val grams: Grams = ngramGenerator.generate(content)
                FileWriter.write(file, cstsPath, cstVectorsPath, grams)
                println("$file: ${grams.size} n-grams extracted")
            }

            val writeTimeLogger = TimeLogger(task_name = "N-grams write")
            FileWriter.write(allNgramsFilePath, ngramGenerator.allNgrams)
            writeTimeLogger.finish()

            timeLogger.finish(fullFinish = true)
            println("${ngramGenerator.allNgrams.size} n-grams extracted")
        }
    }
}