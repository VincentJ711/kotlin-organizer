package com.github.vincentj711.kotlinorganizer.actions

import org.jetbrains.kotlin.fir.resolve.dfa.stackOf
import org.jetbrains.kotlin.psi.KtFunction

class DfsSortAction : SortAction() {
  override fun arrange(fnDeps: Map<KtFunction, Set<KtFunction>>) =
      fnDeps.keys.map { dfs(it, fnDeps) }
          // perhaps dont sort by descending to preserve
          // the original order the user had ?
          .sortedByDescending { it.size }
          .flatten()
          .distinct()

  /** runs dfs for only the given node. the graph is represented by the given
   * function dependencies. the traversal is returned */
  private fun dfs(node: KtFunction, fnDeps: Map<KtFunction, Set<KtFunction>>):
      List<KtFunction> {
    val trav = mutableListOf<KtFunction>()
    val visited = mutableSetOf<KtFunction>()
    val stack = stackOf(node)

    while (stack.size > 0) {
      val n = stack.pop()
      if (n !in visited) {
        visited.add(n)
        trav.add(n)
        // visit the funcs you called first by reversing the deps
        fnDeps[n]?.reversed()?.forEach {
          stack.push(it)
        }
      }
    }

    return trav
  }
}