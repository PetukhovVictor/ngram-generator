package org.jetbrains.kotlin

import com.xenomachina.argparser.ArgParser

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val sourcesPath by parser.storing("-i", "--input", help="path to folder with files for n-gram generation")
    val factorizedSourcesPath by parser.storing("-o", "--output", help="path to folder, in which will be written files with n-grams")
    val allNgramsFilePath by parser.storing("--all_ngrams_file", help="path to files, in which will be written all found n-grams")

    Runner.run(sourcesPath, factorizedSourcesPath, allNgramsFilePath)
}