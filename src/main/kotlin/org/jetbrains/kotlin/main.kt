package org.jetbrains.kotlin

import com.fasterxml.jackson.core.type.TypeReference
import com.xenomachina.argparser.ArgParser
import org.jetbrains.kotlin.generating.Grams
import org.jetbrains.kotlin.generating.NgramGenerator
import org.jetbrains.kotlin.io.JsonFilesReader
import org.jetbrains.kotlin.structures.CstNode
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper



fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val sourcesPath by parser.storing("-p", "--path", help="path to files for n-gram generation")
    val allNgramsFilePath by parser.storing("--all_ngrams_file", help="path to files, in which will be written all found n-grams")
    val selectedNgramsFilePath by parser.storing("--selected_ngrams_file", help="path to files, in which will be written selected n-grams")
    val ngramGenerator = NgramGenerator(d=3)
    val gramsByFiles: MutableMap<String, Grams> = mutableMapOf()
    val cstNodeReference = object: TypeReference<CstNode>() {}

    JsonFilesReader<CstNode>(sourcesPath, "json", cstNodeReference).run { content: CstNode, filename: String ->
        val grams: Grams = ngramGenerator.generate(content)
        print("$filename: ")
        println(grams.size)
        gramsByFiles[filename] = grams
    }

    val mapper = ObjectMapper()
    File(allNgramsFilePath).writeText(mapper.writeValueAsString(ngramGenerator.allNgrams))
    println(ngramGenerator.allNgrams.size)
}