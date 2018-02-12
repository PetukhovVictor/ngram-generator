package org.jetbrains.kotlin.generating

import org.jetbrains.kotlin.structures.AbstractNode
import java.util.*

typealias Gram = MutableList<String>
typealias Grams = MutableList<Gram>
typealias Nodes = MutableList<AbstractNode>

class NgramGenerator(private val n: Int, private val d: Int) {
    private val ngrams: MutableList<Grams> = mutableListOf()

    fun buildNgrams(path: Nodes): Grams {
        val ngrams: Grams = mutableListOf()

        val base = mutableListOf<String>()
        path.withIndex().map {
            val baseNode = it.value.type
            for (j in 1..n) {
                val currentNode = path[it.index + j]
                for (k in j..n) {

                }
            }
        }

        return mutableListOf(base)
    }

    fun buildNgramsByPaths(paths: MutableList<Nodes>): Grams {
        val ngrams: Grams = mutableListOf()
        paths.map { ngrams.addAll(buildNgrams(it)) }
        return ngrams
    }

    private fun findChildrenWithLimit(path: Stack<AbstractNode>, sourceNode: AbstractNode, found: MutableList<Nodes>, limit: Int, alreadyEarlierVisitedNodes: Int, alreadyVisitedNodes: Int = 0) {
        if (limit == 0) {
            return
        }
        val node = path.last()
        if (node.children.size != 0) {
            for (child in node.children) {
                if (child == sourceNode) {
                    continue
                }
                path.push(child)
                findChildrenWithLimit(path, sourceNode, found, limit - 1, alreadyEarlierVisitedNodes, alreadyVisitedNodes + 1)
                path.pop()
            }
        } else {
            found.add(path.toMutableList())
        }
    }

    fun findNgramOnPath(node: AbstractNode, parentNodes: Nodes) {
        val maxChildrenWalkLimit = n * d - 1
        val nodesOnPath: Nodes = mutableListOf()
        val ngrams: Grams = mutableListOf()
        println("For: ${node.type}")

        parentNodes.reversed().withIndex().forEach {
            val prevNodeOnPath = if (nodesOnPath.size != 0) nodesOnPath.last() else node
            val alreadyVisitedNodes = it.index
            val walkLimit = maxChildrenWalkLimit - alreadyVisitedNodes
            val paths: MutableList<Nodes> = mutableListOf()
            val currentCombineNodes: MutableList<Nodes> = mutableListOf()
            val path: Stack<AbstractNode> = Stack()

            path.push(it.value)
            findChildrenWithLimit(path, prevNodeOnPath, paths, walkLimit, alreadyVisitedNodes + 1)

            paths.forEach {
                currentCombineNodes.add((nodesOnPath + it).toMutableList())
            }

            nodesOnPath.add(it.value)

            ngrams.addAll(buildNgramsByPaths(currentCombineNodes))

            println("Parent: ${it.value}")
            println(currentCombineNodes)
            println("----------------------")
        }
        println(ngrams)
        println("======================")
    }

    private fun dfw(node: AbstractNode, parentNodes: Nodes) {
        parentNodes.add(node)
        if (parentNodes.size > n * d) {
            parentNodes.removeAt(0)
        }
        if (node.children.size != 0) {
            for ((index, child) in node.children.withIndex()) {
                findNgramOnPath(child, parentNodes)
                dfw(child, parentNodes)
            }
        }
        parentNodes.remove(node)
    }

    fun generate(tree: AbstractNode) {
        val parentNodes: Nodes = mutableListOf()
        dfw(tree, parentNodes)
        println(ngrams)
        println("==================")
    }
}