package org.jetbrains.kotlin.structures

abstract class CstNode: AbstractNode() {
    abstract val chars: String
    abstract val children: ArrayList<CstNode>
}