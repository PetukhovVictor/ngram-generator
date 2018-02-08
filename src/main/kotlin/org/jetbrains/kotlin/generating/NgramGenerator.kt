package org.jetbrains.kotlin.generating

import org.jetbrains.kotlin.structures.AbstractNode
import java.util.*
import kotlin.collections.ArrayList

typealias TWalkHistory = Stack<Pair<Int, AbstractNode>>

class NgramGenerator<in T: AbstractNode>(private val window: Int) {
    private val statistic: Map<String, Int> = mutableMapOf()

    private fun generateBigrams(walkHistory: TWalkHistory, source: AbstractNode): MutableList<MutableList<AbstractNode>> {
        var currentWindow = window
        val unigrams = mutableListOf<AbstractNode>()
        val ngrams = mutableListOf<MutableList<AbstractNode>>()

        while (currentWindow != 0 && walkHistory.size != 0) {
            val walkingNode = walkHistory.pop()
            unigrams.add(walkingNode.second)
            currentWindow--
            dfwLimited(walkingNode.second, unigrams, currentWindow, mutableListOf(walkingNode.second, source))
        }

        unigrams.map { ngrams.add(mutableListOf(it)) }

        return ngrams
    }

    private fun generateNgrams(ngrams: MutableList<MutableList<AbstractNode>>, source: AbstractNode): MutableList<MutableList<AbstractNode>> {
        val ngramsContinued = mutableListOf<MutableList<AbstractNode>>()

        for (ngram in ngrams) {
            val ngramContinuation = mutableListOf<AbstractNode>()
            val ngramCopied = ngram.toMutableList()
            ngramCopied.add(source)
            dfwLimited(ngram.last(), ngramContinuation, window, ngramCopied)

            ngramContinuation.map {
                val ngramContinued = ngram.toMutableList()
                ngramContinued.add(it)
                ngramsContinued.add(ngramContinued)
            }
        }

        return ngramsContinued
    }

    private fun findNgramsOnPath(walkHistory: TWalkHistory, source: AbstractNode): List<MutableList<AbstractNode>> {
        val bigrams = generateBigrams(walkHistory, source)
        val threegrams = generateNgrams(bigrams, source)
//      val threegrams = generateNgrams(bigrams)

        return bigrams + threegrams
    }

    private fun dfwLimited(node: AbstractNode, found: MutableList<AbstractNode>, limit: Int, excludeNodes: MutableList<AbstractNode>) {
        if (limit == 0) {
            return
        }
        if (node.children.size != 0) {
            for (child in node.children) {
                if (excludeNodes.contains(child)) {
                    continue
                }

                found.add(child)
                dfwLimited(child, found, limit - 1, excludeNodes)
            }
        }
    }

    private fun dfw(ngrams: MutableList<MutableList<AbstractNode>>, node: AbstractNode, walkHistory: TWalkHistory) {
        if (node.children.size != 0) {
            for ((index, child) in node.children.withIndex()) {
                walkHistory.push(Pair(index, node))
                val clonedStack = Stack<Pair<Int, AbstractNode>>()
                clonedStack.addAll(walkHistory)
                val ngramsForNode = findNgramsOnPath(clonedStack, child)
                ngramsForNode.map { it.add(child) }
                ngrams += ngramsForNode
                dfw(ngrams, child, walkHistory)
                walkHistory.pop()
            }
        }
    }

    fun generate(structure: T) {
        val ngrams = mutableListOf<MutableList<AbstractNode>>()
        dfw(ngrams, structure, Stack())
        println(ngrams)
        println("==================")
    }
}