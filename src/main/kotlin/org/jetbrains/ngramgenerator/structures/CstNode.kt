package org.jetbrains.ngramgenerator.structures

import java.util.ArrayList

class CstNode: AbstractNode() {
    var chars: String = ""
    override val children: ArrayList<CstNode> = arrayListOf()

    override fun toString(): String {
        return this.type
    }
}