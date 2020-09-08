package com.github.vincentj711.kotlinorganizer

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationContainer
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtTypeAlias
import java.util.LinkedList

private typealias PsiEltChanged = Boolean

class SortAction(
    /** allow tests to provide a config */
    private val config: Config = ServiceManager.getService(Config::class.java)
) : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val vfile = e.dataContext.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
    val proj = e.project ?: return
    val psiFiles = mutableListOf<PsiFile>()

    if (vfile.extension == "kt") {
      psiFiles.add(vfile.toPsiFile(proj) ?: return)
    } else if (vfile.isDirectory &&
        RecursiveSortConfirmation(proj).showAndGet()) {
      val q = LinkedList<VirtualFile>()

      vfile.children.forEach { q.add(it) }

      while (q.isNotEmpty()) {
        val vf = q.remove()

        if (vf.extension == "kt") {
          val psiFile = vf.toPsiFile(proj)
          if (psiFile != null) {
            psiFiles.add(psiFile)
          }
        } else if (vf.isDirectory) {
          vf.children.forEach { q.add(it) }
        }
      }
    }

    if (psiFiles.isNotEmpty()) {
      WriteCommandAction.runWriteCommandAction(
          proj, "recursive kotlin sort", null, Runnable {
        psiFiles.filterIsInstance<KtDeclarationContainer>().forEach {
          if (sort(it as PsiFile, it.declarations)) {
            CodeStyleManager.getInstance(proj).reformat(it)
          }
        }
      }, *psiFiles.toTypedArray())
    }

    if (psiFiles.size > 1) {
      Notification("Kotlin Organizer", "", "done with recursive kotlin sorting",
          NotificationType.INFORMATION).notify(proj)
    }
  }

  private fun sort(ctr: PsiElement, decls: List<KtDeclaration>): PsiEltChanged {
    var changed = false

    decls.filterIsInstance<KtClassOrObject>()
        .forEach { changed = changed || sort(it, it.declarations) }

    val groups = groupDeclarations(decls)
    val funcs = groups[Group.FUNCTIONS] ?: emptyList()

    groups[Group.FUNCTIONS] = when (config.fnOrderStrategy) {
      Config.FnOrderStrategy.DFS ->
        DFSFunctionSorter.order(getFunctionDependencies(decls))
      Config.FnOrderStrategy.BFS ->
        BFSFunctionSorter.order(getFunctionDependencies(decls))
      Config.FnOrderStrategy.ALPHABETICAL ->
        funcs.sortedBy { it.name }
      Config.FnOrderStrategy.PRESERVE -> funcs
    }

    val after = groups.keys
        .sortedBy { g -> config.groupOrdering[g] ?: 1000 }
        .map { g -> groups[g]!! }
        .flatten()

    val sortIsValid = decls
        .sortedBy { it.hashCode() } == after.sortedBy { it.hashCode() }

    if (decls != after && sortIsValid) {
      if (ctr is KtClassOrObject) {
        after.forEach { ctr.addDeclaration(it) }
      } else {
        after.forEach { ctr.add(it) }
      }
      decls.forEach { it.delete() }
      return true
    }

    return changed
  }

  private fun groupDeclarations(decls: List<KtDeclaration>):
      LinkedHashMap<Group, List<KtDeclaration>> {
    val groups = LinkedHashMap<Group, MutableList<KtDeclaration>>()
    val ret = LinkedHashMap<Group, List<KtDeclaration>>()

    decls.forEach { decl ->
      val group = getGroup(decl)
      val l = groups[group] ?: mutableListOf()
      l.add(decl)
      groups[group] = l
    }

    for ((k, v) in groups) {
      ret[k] = v.toList()
    }

    return ret
  }

  private fun getGroup(decl: KtDeclaration): Group {
    return when {
      decl is KtTypeAlias -> Group.TYPE_ALIASES
      decl is KtEnumEntry -> Group.ENUM_ENTRIES
      decl is KtProperty -> Group.SETUP
      decl is KtSecondaryConstructor -> Group.SETUP
      decl is KtClassInitializer -> Group.SETUP
      decl is KtNamedFunction -> Group.FUNCTIONS
      decl is KtClass && decl.isInterface() -> Group.INTERFACES
      decl is KtClass && decl.isEnum() -> Group.ENUMS
      decl is KtClass -> Group.CLASSES
      decl is KtObjectDeclaration && decl.isCompanion() ->
        Group.COMPANION_OBJECTS
      decl is KtObjectDeclaration -> Group.OBJECTS
      else -> Group.OTHER
    }
  }

  private fun getFunctionDependencies(decls: List<KtDeclaration>)
      : Map<KtNamedFunction, LinkedHashSet<KtNamedFunction>> {
    val deps = linkedMapOf<KtNamedFunction, LinkedHashSet<KtNamedFunction>>()

    decls.filterIsInstance<KtNamedFunction>().forEach { fn ->
      val tmp = LinkedHashSet<KtNamedFunction>()
      (fn as KtFunction).accept(FunctionWalker(fn, decls, tmp))
      deps[fn] = tmp
    }

    return deps
  }
}

