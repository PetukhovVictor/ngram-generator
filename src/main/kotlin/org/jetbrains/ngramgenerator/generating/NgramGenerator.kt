package org.jetbrains.ngramgenerator.generating

import org.jetbrains.ngramgenerator.structures.AbstractNode
import java.util.*

typealias Gram = String
typealias Grams = MutableSet<Gram>
typealias GramsStatistic = MutableMap<String, Int>
typealias Nodes = MutableList<AbstractNode>

class NgramGenerator(private val d: Int) {
    val allNgrams: GramsStatistic = mutableMapOf()
    private val n = 3
    private val ngrams: Grams = mutableSetOf()

    private fun ngramAdd(gram: List<String>) {
        ngrams.add(gram.joinToString(":"))
    }

    private fun buildNgramsByPath(path: LinkedHashSet<AbstractNode>, firstNodeOnPath: AbstractNode) {
        val lastNodeOnPath = path.last()
        val distanceBetweenFirstAndLast = path.size - 1
        if (distanceBetweenFirstAndLast <= d) {
            ngramAdd(listOf(firstNodeOnPath.type, distanceBetweenFirstAndLast.toString(), lastNodeOnPath.type)) // add bigram
        }
        path.withIndex().forEach {
            if (it.value != lastNodeOnPath) {
                val distanceToFirst = it.index
                val distanceToLast = path.size - 2 - it.index
                if (distanceToFirst <= d && distanceToLast <= d) {
                    ngramAdd(listOf(firstNodeOnPath.type, distanceToFirst.toString(), it.value.type, distanceToLast.toString(), lastNodeOnPath.type)) // add 3-gram
                }
            }
        }
    }

    private fun buildNgrams(node: AbstractNode, path: Nodes) {
        val visitedNodes = linkedSetOf<AbstractNode>()
        val usedNodes = linkedSetOf<AbstractNode>()
        var visitedAreaStarted = false
        path.asReversed().forEach {
            val isVisited = visitedNodes.contains(it)
            if (!isVisited) {
                if (visitedAreaStarted) {
                    visitedAreaStarted = false
                    usedNodes.forEach { visitedNodes.remove(it) }
                    visitedNodes.add(usedNodes.last())
                    usedNodes.clear()
                }
                visitedNodes.add(it)
                if (visitedNodes.size < n * d - 1) {
                    buildNgramsByPath(visitedNodes, node)
                }
            } else if (!visitedAreaStarted) {
                visitedAreaStarted = true
            }

            if (isVisited) {
                usedNodes.add(it)
            }
        }
    }

    private fun dfw(node: AbstractNode, path: Nodes) {
        ngramAdd(listOf(node.type)) // add unigram
        buildNgrams(node, path) // build bigram and 3-gram

        path.add(node)
        node.children.map {
            dfw(it, path)
            path.add(node)
        }
        if (node.children.size == 0) {
            path.add(node)
        }
    }

    fun generate(tree: AbstractNode): Grams {
        ngrams.clear()
        dfw(tree, mutableListOf())

        ngrams.forEach {
            if (allNgrams.contains(it)) {
                allNgrams[it] = allNgrams[it]!!.inc()
            } else {
                allNgrams[it] = 1
            }
        }

        return ngrams
    }
}