package org.jetbrains.kotlin.generating

import org.jetbrains.kotlin.structures.AbstractNode
import java.util.*
import kotlin.math.min

typealias Grams = MutableList<String>
typealias GramAsNode = List<AbstractNode>
typealias GramsAsNode = MutableList<GramAsNode>
typealias Nodes = MutableList<AbstractNode>
typealias NodesSet = MutableList<Nodes>

class NgramGenerator(private val d: Int) {
    private val n = 3
    private val ngrams: GramsAsNode = mutableListOf()

    companion object {
        fun gramsStringify(gramsAsNode: List<GramAsNode>): Grams {
            val grams: Grams = mutableListOf()

            gramsAsNode.map {
                grams.add(it.joinToString(":"))
            }

            return grams
        }
    }

    fun buildNgrams(path: GramAsNode) {
        path.withIndex().map {
            val baseNode = it.value
            val firstOffset = it.index + 1
            for (i in firstOffset..min(firstOffset + d, path.size - 1)) {
                val secondOffset = i + 1
                ngrams.add(listOf(baseNode, path[i]))
                for (j in secondOffset..min(secondOffset + d, path.size - 1)) {
                    ngrams.add(listOf(baseNode, path[i], path[j]))
                }
            }
        }
    }

    fun buildNgramsByPaths(paths: GramsAsNode) {
        paths.map { buildNgrams(it) }
    }

    private fun findChildrenWithLimit(path: Stack<AbstractNode>, sourceNode: AbstractNode, found: NodesSet, limit: Int, alreadyEarlierVisitedNodes: Int, alreadyVisitedNodes: Int = 0) {
        if (limit == 0) {
            return
        }
        val node = path.last()
        node.children.map map@{
            if (it == sourceNode) {
                return@map
            }
            path.push(it)
            findChildrenWithLimit(path, sourceNode, found, limit - 1, alreadyEarlierVisitedNodes, alreadyVisitedNodes + 1)
            path.pop()
        }
        if (node.children.size == 0) {
            found.add(path.toMutableList())
        }
    }

    fun findNgramOnPath(node: AbstractNode, parentNodes: GramAsNode) {
        val maxChildrenWalkLimit = n * d - 1
        val nodesOnPath: Nodes = mutableListOf()

        parentNodes.reversed().withIndex().forEach {
            val prevNodeOnPath = if (nodesOnPath.isNotEmpty()) nodesOnPath.last() else node
            val alreadyVisitedNodes = it.index
            val walkLimit = maxChildrenWalkLimit - alreadyVisitedNodes
            val paths: NodesSet = mutableListOf()
            val path: Stack<AbstractNode> = Stack()
            val currentCombineNodes: GramsAsNode = mutableListOf()

            path.push(it.value)
            findChildrenWithLimit(path, prevNodeOnPath, paths, walkLimit, alreadyVisitedNodes + 1)

            paths.forEach {
                currentCombineNodes.add(nodesOnPath + it)
            }

            nodesOnPath.add(it.value)
            buildNgramsByPaths(currentCombineNodes)
        }
    }

    private fun dfw(node: AbstractNode, parentNodes: Nodes) {
        ngrams.add(listOf(node))
        parentNodes.add(node)
        if (parentNodes.size > n * d) {
            parentNodes.removeAt(0)
        }
        node.children.map {
            findNgramOnPath(it, parentNodes)
            dfw(it, parentNodes)
        }
        parentNodes.remove(node)
    }

    fun generate(tree: AbstractNode): List<GramAsNode> {
        dfw(tree, mutableListOf())
        return ngrams.distinct()
    }
}