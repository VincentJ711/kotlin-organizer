package com.github.vincentj711.kotlinorganizer

import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.util.LinkedList
import java.util.Queue

object BFSFunctionSorter {
  fun order(deps: Map<KtNamedFunction, LinkedHashSet<KtNamedFunction>>) =
      deps.keys
          .map { bfs(it, deps) }
          .sortedByDescending { it.size }
          .flatten()
          .distinct()

  private fun bfs(
      node: KtNamedFunction,
      deps: Map<KtNamedFunction, LinkedHashSet<KtNamedFunction>>
  ): List<KtNamedFunction> {
    val trav = mutableListOf<KtNamedFunction>()
    val visited = mutableSetOf<KtDeclaration>()
    val queue: Queue<KtDeclaration> = LinkedList()
    queue.add(node)

    while (queue.size > 0) {
      val n = queue.remove()

      if (n !in visited) {
        visited.add(n)

        if (n is KtNamedFunction) {
          trav.add(n)
        }

        deps[n]?.forEach { queue.add(it) }
      }
    }

    return trav
  }
}