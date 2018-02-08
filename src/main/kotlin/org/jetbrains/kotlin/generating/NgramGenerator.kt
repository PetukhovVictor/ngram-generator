package org.jetbrains.kotlin.generating

import org.jetbrains.kotlin.structures.AbstractNode

class NgramGenerator<in T: AbstractNode> {
    private val statistic: Map<String, Int> = mutableMapOf()

    private fun walk(structure: AbstractNode) {
        println(structure.type)
        if (structure.children.size != 0) {
            for (child in structure.children) {
                walk(child)
            }
        }
    }

    fun generate(structure: T) {
        walk(structure)
        println("==================")
    }
}