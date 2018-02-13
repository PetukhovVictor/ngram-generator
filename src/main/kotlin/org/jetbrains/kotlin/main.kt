package org.jetbrains.kotlin

import com.fasterxml.jackson.core.type.TypeReference
import com.xenomachina.argparser.ArgParser
import org.jetbrains.kotlin.generating.GramAsNode
import org.jetbrains.kotlin.generating.Grams
import org.jetbrains.kotlin.generating.NgramGenerator
import org.jetbrains.kotlin.io.JsonFilesReader
import org.jetbrains.kotlin.structures.CstNode

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val path by parser.storing("-p", "--path", help="path to files for n-gram generation")
    val ngramGenerator = NgramGenerator(d=3)
    var allUniqueGrams: Set<String> = mutableSetOf()
    val gramsByFiles: MutableMap<String, Grams> = mutableMapOf()
    val cstNodeReference = object: TypeReference<CstNode>() {}

    JsonFilesReader<CstNode>(path, "json", cstNodeReference).run { content: CstNode, filename: String ->
        val grams: List<GramAsNode> = ngramGenerator.generate(content)
        val gramsAsString: Grams = NgramGenerator.gramsStringify(grams)

        allUniqueGrams = allUniqueGrams.union(gramsAsString)
        gramsByFiles[filename] = gramsAsString
    }

    println(gramsByFiles)
}