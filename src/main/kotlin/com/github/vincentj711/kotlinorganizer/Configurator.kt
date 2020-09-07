package com.github.vincentj711.kotlinorganizer

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.SearchableConfigurable

class Configurator : SearchableConfigurable {
  private val config = ServiceManager.getService(Config::class.java)
  private val configUI = ConfigUI(config)

  override fun isModified(): Boolean {
    return config.fnOrderStrategy != configUI.fnOrderStrategy ||
        config.groupOrdering != configUI.groupOrdering
  }

  override fun apply() {
    config.groupOrdering = configUI.groupOrdering
    config.fnOrderStrategy = configUI.fnOrderStrategy
  }

  override fun createComponent() = configUI.mainPanel
  override fun getDisplayName() = "Kotlin Organizer"
  override fun getId() = "ktOrganizerConfigurator"
}

