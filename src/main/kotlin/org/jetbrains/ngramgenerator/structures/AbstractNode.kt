package org.jetbrains.ngramgenerator.structures

abstract class AbstractNode {
    val type: String = ""
    abstract val children: ArrayList<out AbstractNode>
}