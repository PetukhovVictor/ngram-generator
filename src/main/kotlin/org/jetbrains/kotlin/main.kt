package org.jetbrains.kotlin

import com.fasterxml.jackson.core.type.TypeReference
import com.xenomachina.argparser.ArgParser
import org.jetbrains.kotlin.generating.NgramGenerator
import org.jetbrains.kotlin.io.JsonFilesReader
import org.jetbrains.kotlin.structures.CstNode

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val path by parser.storing("-p", "--path", help="path to files for n-gram generation")
    val ngramGenerator = NgramGenerator<CstNode>(window=3)

    JsonFilesReader<CstNode>(path, "json", object: TypeReference<CstNode>() {}).run {
        ngramGenerator.generate(it)
//        FileWriter(ngrams)
    }
}