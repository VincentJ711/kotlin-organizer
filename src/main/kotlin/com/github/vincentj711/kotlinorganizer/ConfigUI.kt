package com.github.vincentj711.kotlinorganizer

import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.ButtonGroup
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton

/**
 * see <a href="https://jetbrains.org/intellij/sdk/docs/user_interface_components/lists_and_trees.html">here</a>
 * for info on ToolbarDecorator/JBList/CollectionListModel
 */
class ConfigUI(initialConfig: Config) {
  private val fnPane = JPanel(GridLayout(5, 1))
  private val fnPaneTitle = JLabel("Function Order Strategy")
  private val fnPaneButtonGroup = ButtonGroup()
  private val fnPaneButtons: List<FnOrderStrategyButton>

  init {
    fnPane.add(fnPaneTitle)
    fnPaneTitle.border = BorderFactory.createEmptyBorder(0, 0, 8, 0)

    fnPaneButtons = Config.FnOrderStrategy.values().map {
      val btn = FnOrderStrategyButton(it)
      btn.isSelected = initialConfig.fnOrderStrategy == it
      fnPaneButtonGroup.add(btn)
      fnPane.add(btn)
      btn
    }
  }

  private val groupingPaneWrapper = JPanel(BorderLayout(0, 10))
  private val groupingPaneTitle = JLabel("Group Ordering")
  private val groupingPaneListModel = CollectionListModel<Group>()
  private val groupingPaneList = JBList(groupingPaneListModel)
  private val groupingPane = ToolbarDecorator.createDecorator(groupingPaneList)
      .disableRemoveAction()
      .createPanel()

  init {
    initialConfig.groupOrdering
        .toList()
        .sortedBy { it.second }
        .map { it.first }
        .forEach { groupingPaneListModel.add(it) }

    groupingPaneList.setCellRenderer { jList, g, _, isSelected, _ ->
      val c = if (isSelected) jList.selectionForeground else jList.foreground
      val label = JLabel(g.uiText)
      label.foreground = c
      label.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
      label
    }

    groupingPaneWrapper.add(groupingPaneTitle, BorderLayout.NORTH)
    groupingPaneWrapper.add(groupingPane, BorderLayout.CENTER)
  }

  val mainPanel = JPanel(BorderLayout(0, 20))

  init {
    mainPanel.add(fnPane, BorderLayout.NORTH)
    mainPanel.add(groupingPaneWrapper, BorderLayout.CENTER)
  }

  val fnOrderStrategy: Config.FnOrderStrategy
    get() = fnPaneButtons.first { it.isSelected }.strategy

  val groupOrdering: Map<Group, Int>
    get() {
      val nextOrdering = mutableMapOf<Group, Int>()
      groupingPaneListModel.items.mapIndexed { i, g -> nextOrdering[g] = i }
      return nextOrdering
    }

  private class FnOrderStrategyButton(val strategy: Config.FnOrderStrategy) :
      JRadioButton(strategy.uiText)
}