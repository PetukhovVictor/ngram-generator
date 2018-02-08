package org.jetbrains.kotlin

import com.xenomachina.argparser.ArgParser
import org.jetbrains.kotlin.generating.NgramGenerator
import org.jetbrains.kotlin.io.FileWriter
import org.jetbrains.kotlin.io.JsonFilesReader
import org.jetbrains.kotlin.structures.CstNode

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val path by parser.storing("-p", "--path", help = "path to files for n-gram generation")
    val ngramGenerator = NgramGenerator<CstNode>()

    JsonFilesReader<CstNode>(path, "json").run {
        ngramGenerator.generate(it)
//        FileWriter(ngrams)
    }
}