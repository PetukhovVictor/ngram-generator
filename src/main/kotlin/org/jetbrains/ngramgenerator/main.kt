package org.jetbrains.ngramgenerator

import com.xenomachina.argparser.ArgParser

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val cstsPath by parser.storing("-i", "--input", help="path to folder with files for n-gram generation")
    val cstVectorsPath by parser.storing("-o", "--output", help="path to folder, in which will be written files with n-grams")

    Runner.run(cstsPath, cstVectorsPath)
}