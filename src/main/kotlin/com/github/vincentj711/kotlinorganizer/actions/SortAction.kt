package com.github.vincentj711.kotlinorganizer.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import org.jetbrains.kotlin.idea.references.KtInvokeFunctionReference
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationContainer
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

abstract class SortAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    println("running ${this.javaClass.simpleName}")
    val psiFile = e.getData(LangDataKeys.PSI_FILE) ?: return

    if (psiFile is KtDeclarationContainer) {
      WriteCommandAction.runWriteCommandAction(e.project ?: return) {
        sort(psiFile, psiFile.declarations)
      }
    }
  }

  /** takes a container and its ktdeclarations and does some sorting on
   * the containers declarations */
  private fun sort(ctr: PsiElement, decls: List<KtDeclaration>) {
    val fnDeps = linkedMapOf<KtFunction, LinkedHashSet<KtFunction>>()

    decls.forEach {
      if (it is KtClassOrObject) {
        sort(it, it.declarations)
      } else if (it is KtFunction) {
        // determine the functions at the same level as it that it calls
        val deps = linkedSetOf<KtFunction>()
        it.accept(KtFunctionWalker(it, decls, deps))
        fnDeps[it] = deps
      }
    }

    val before = fnDeps.keys.toList()
    val after = arrange(fnDeps)
    val valid = before.sortedBy { it.hashCode() } ==
        after.sortedBy { it.hashCode() }

    if (before != after && valid) {
      /* modify the psi
        Note: for some reason i noticed if i call ctr.add for a ktclassorobject,
        the functions get added after the class body, so differentiate the two
        cases */
      if (ctr is KtClassOrObject) {
        after.forEach { ctr.addDeclaration(it) }
      } else {
        after.forEach { ctr.add(it) }
      }
      before.forEach { it.delete() }
    }
  }

  protected abstract fun arrange(
      fnDeps: Map<KtFunction, LinkedHashSet<KtFunction>>): List<KtFunction>

  private class KtFunctionWalker(
      val fn: KtFunction,
      val decls: List<KtDeclaration>,
      val deps: LinkedHashSet<KtFunction>
  ) : PsiRecursiveElementVisitor() {
    override fun visitElement(el: PsiElement?) {
      super.visitElement(el)

      // leave early if its not a ref expression since
      // calling el.references is incredibly time expensive
      if (el !is KtReferenceExpression) {
        return
      }

      el.references.filterIsInstance<KtInvokeFunctionReference>().forEach {
        val expr = it.element.getCallNameExpression() ?: return@forEach
        val ref = expr.resolve() ?: return@forEach
        if (ref is KtFunction && ref != fn && decls.contains(ref)) {
          deps.add(ref)
        }
      }
    }
  }
}