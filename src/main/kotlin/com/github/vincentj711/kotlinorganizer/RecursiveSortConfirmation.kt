package com.github.vincentj711.kotlinorganizer

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class RecursiveSortConfirmation(proj: Project) : DialogWrapper(proj) {
  init {
    init()
    title = "Confirmation"
  }

  override fun createCenterPanel(): JComponent? {
    val dialogPanel = JPanel(BorderLayout())
    val label = JLabel("sort all the .kt files in this directory")
    label.preferredSize = Dimension(200, 100)
    dialogPanel.add(label, BorderLayout.CENTER)
    return dialogPanel
  }
}