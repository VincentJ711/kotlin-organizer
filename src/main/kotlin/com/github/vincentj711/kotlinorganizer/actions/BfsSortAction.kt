package com.github.vincentj711.kotlinorganizer.actions

import org.jetbrains.kotlin.psi.KtFunction
import java.util.LinkedList
import java.util.Queue

class BfsSortAction : SortAction() {
  override fun arrange(fnDeps: Map<KtFunction, Set<KtFunction>>) =
      fnDeps.keys.map { bfs(it, fnDeps) }
          // perhaps dont sort by descending to preserve
          // the original order the user had ?
          .sortedByDescending { it.size }
          .flatten()
          .distinct()

  /** runs bfs for only the given node. the graph is represented by the given
   * function dependencies. the traversal is returned */
  private fun bfs(node: KtFunction, fnDeps: Map<KtFunction, Set<KtFunction>>):
      List<KtFunction> {
    val trav = mutableListOf<KtFunction>()
    val visited = mutableSetOf<KtFunction>()
    val queue: Queue<KtFunction> = LinkedList()
    queue.add(node)

    while (queue.size > 0) {
      val n = queue.remove()
      if (n !in visited) {
        visited.add(n)
        trav.add(n)
        fnDeps[n]?.forEach {
          queue.add(it)
        }
      }
    }

    return trav
  }
}