package org.jetbrains.kotlin.generating

import org.jetbrains.kotlin.structures.AbstractNode
import org.jetbrains.kotlin.structures.CstNode

class NgramGenerator<in T: AbstractNode> {
    private val statistic: Map<String, Int> = mutableMapOf()

    fun generate(structure: T) {
        println(structure.type)
    }
}