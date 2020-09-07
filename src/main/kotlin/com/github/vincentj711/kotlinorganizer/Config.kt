package com.github.vincentj711.kotlinorganizer

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "KtOrganizerConfig",
    storages = [Storage("KtOrganizerConfig.xml")]
)
data class Config(
    var fnOrderStrategy: FnOrderStrategy = FnOrderStrategy.PRESERVE,
    var groupOrdering: Map<Group, Int> = Group.defaultOrdering
) : PersistentStateComponent<Config> {
  override fun getState() = this

  override fun loadState(config: Config) =
      XmlSerializerUtil.copyBean(config, this)

  enum class FnOrderStrategy(desc: String? = null) {
    DFS,
    BFS,
    ALPHABETICAL,
    PRESERVE("preserves the order the functions were declared in.");

    val uiText = name.replace("_", " ").toLowerCase() +
        if (desc != null) " - $desc" else ""
  }
}