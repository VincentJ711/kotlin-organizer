package com.github.vincentj711.kotlinorganizer

enum class Group(val description: String? = null) {
  COMPANION_OBJECTS,
  SETUP("includes properties, init blocks and secondary constructors"),
  FUNCTIONS,
  INTERFACES,
  ENUMS,
  OBJECTS,
  CLASSES,
  OTHER;

  companion object {
    /** enums declared first will appear in the file first */
    val defaultOrdering = mapOf(
        *values().mapIndexed { ind, g -> Pair(g, ind) }.toTypedArray())
  }

  /** whats displayed in the settings dialog for this group */
  val displayName = name.toLowerCase().replace("_", " ")
}