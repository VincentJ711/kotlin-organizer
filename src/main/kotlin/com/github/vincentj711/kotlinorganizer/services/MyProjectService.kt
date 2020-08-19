package com.github.vincentj711.kotlinorganizer.services

import com.github.vincentj711.kotlinorganizer.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

  init {
    println(MyBundle.message("projectService", project.name))
  }
}
