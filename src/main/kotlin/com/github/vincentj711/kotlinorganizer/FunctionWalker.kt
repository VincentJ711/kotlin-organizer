package com.github.vincentj711.kotlinorganizer

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import org.jetbrains.kotlin.idea.references.KtInvokeFunctionReference
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

class FunctionWalker(
    private val fn: KtNamedFunction,
    private val decls: List<KtDeclaration>,
    private val deps: LinkedHashSet<KtNamedFunction>
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
      if (ref is KtNamedFunction && ref != fn && decls.contains(ref)) {
        deps.add(ref)
      }
    }
  }
}