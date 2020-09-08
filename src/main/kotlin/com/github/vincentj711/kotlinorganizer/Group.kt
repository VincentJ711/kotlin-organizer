package com.github.vincentj711.kotlinorganizer

enum class Group(val description: String? = null) {
  ENUM_ENTRIES("this group should be ordered first."),
  COMPANION_OBJECTS,
  TYPE_ALIASES,
  SETUP("includes properties, init blocks and secondary constructors"),
  FUNCTIONS,
  INTERFACES,
  ENUMS,
  OBJECTS,
  CLASSES,
  OTHER;

  companion object {
    /** enums declared higher will appear higher in the output file */
    val defaultOrdering = mapOf(
        *values().mapIndexed { ind, g -> Pair(g, ind) }.toTypedArray())
  }

  private val displayName = name.toLowerCase().replace("_", " ")

  val uiText = displayName + if (description == null) "" else
    " - $description"
}