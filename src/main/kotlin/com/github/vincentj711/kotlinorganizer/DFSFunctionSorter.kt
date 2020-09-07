package com.github.vincentj711.kotlinorganizer

import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.util.Stack

object DFSFunctionSorter {
  fun order(deps: Map<KtNamedFunction, LinkedHashSet<KtNamedFunction>>) =
      deps.keys
          .map { dfs(it, deps) }
          .sortedByDescending { it.size }
          .flatten()
          .distinct()

  private fun dfs(
      node: KtNamedFunction,
      deps: Map<KtNamedFunction, LinkedHashSet<KtNamedFunction>>
  ): List<KtNamedFunction> {
    val trav = mutableListOf<KtNamedFunction>()
    val visited = mutableSetOf<KtDeclaration>()
    val stack = Stack<KtDeclaration>()
    stack.add(node)

    while (stack.size > 0) {
      val n = stack.pop()
      if (n !in visited) {
        visited.add(n)

        if (n is KtNamedFunction) {
          trav.add(n)
        }

        // visit the dependencies in the order they're made
        deps[n]?.reversed()?.forEach { stack.push(it) }
      }
    }

    return trav
  }
}