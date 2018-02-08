package org.jetbrains.kotlin.structures

import java.util.ArrayList

class CstNode: AbstractNode() {
    var chars: String = ""
    override val children: ArrayList<CstNode> = arrayListOf()
}