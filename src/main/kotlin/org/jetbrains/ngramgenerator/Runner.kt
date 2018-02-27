package org.jetbrains.ngramgenerator

import com.fasterxml.jackson.core.type.TypeReference
import org.jetbrains.ngramgenerator.generating.Grams
import org.jetbrains.ngramgenerator.generating.NgramGeneratorByList
import org.jetbrains.ngramgenerator.generating.NgramGeneratorByTree
import org.jetbrains.ngramgenerator.helpers.TimeLogger
import org.jetbrains.ngramgenerator.io.DirectoryWalker
import org.jetbrains.ngramgenerator.io.FileWriter
import org.jetbrains.ngramgenerator.io.JsonFilesReader
import org.jetbrains.ngramgenerator.structures.Tree
import java.io.File

enum class StructureType {
    TREE, LIST
}

object Runner {
    private const val allNgramsFilePath = "./all_ngrams.json"

    private fun generateByTree(treesPath: String, treeVectorsPath: String) {
        val ngramGenerator = NgramGeneratorByTree(d = 0)
        val treeReference = object: TypeReference<ArrayList<Tree>>() {}
        val timeLogger = TimeLogger(task_name = "N-gram extraction")

        JsonFilesReader<ArrayList<Tree>>(treesPath, "json", treeReference).run { content: ArrayList<Tree>, file: File ->
            val grams: Grams = ngramGenerator.generate(content[0])
            FileWriter.write(file, treesPath, treeVectorsPath, grams)
            println("$file: ${grams.size} n-grams extracted")
        }

        val writeTimeLogger = TimeLogger(task_name = "N-grams write")
        FileWriter.write(allNgramsFilePath, ngramGenerator.allNgrams)
        writeTimeLogger.finish()

        timeLogger.finish(fullFinish = true)
        println("${ngramGenerator.allNgrams.size} n-grams extracted")
    }

    private fun generateByList(listsPath: String, listVectorsPath: String) {
        val ngramGenerator = NgramGeneratorByList(d = 0)
        val listReference = object: TypeReference<Map<String, List<String>>>() {}

        DirectoryWalker(listsPath, maxDepth = 2).run {
            if (it.isDirectory) {
                val repoIdentifier = it.relativeTo(File(listsPath)).invariantSeparatorsPath.split("/")
                if (repoIdentifier.size == 2) {
                    var gramsByRepo = 0
                    JsonFilesReader<Map<String, List<String>>>(it.absolutePath, "class.json", listReference).run { content: Map<String, List<String>>, file: File ->
                        val list = NgramGeneratorByList.linearizeMapOfList(content)
                        val grams: Grams = ngramGenerator.generate(list)
                        FileWriter.write(file, listsPath, listVectorsPath, grams)
                        gramsByRepo += grams.size
                    }
                    println("$repoIdentifier: $gramsByRepo n-grams extracted")
                }
            }
        }

        val writeTimeLogger = TimeLogger(task_name = "N-grams write")
        FileWriter.write(allNgramsFilePath, ngramGenerator.allNgrams)
        writeTimeLogger.finish()
        println("${ngramGenerator.allNgrams.size} n-grams extracted")
    }

    fun run(structureType: StructureType, structuresPath: String, structureVectorsPath: String) {
        when (structureType) {
            StructureType.TREE -> generateByTree(structuresPath, structureVectorsPath)
            StructureType.LIST -> generateByList(structuresPath, structureVectorsPath)
        }
    }
}