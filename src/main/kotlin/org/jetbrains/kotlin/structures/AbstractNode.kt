package org.jetbrains.kotlin.structures

abstract class AbstractNode {
    val type: String = ""
    abstract val children: ArrayList<out AbstractNode>
}