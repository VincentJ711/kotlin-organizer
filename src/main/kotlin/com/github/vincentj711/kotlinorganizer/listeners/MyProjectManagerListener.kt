package com.github.vincentj711.kotlinorganizer.listeners

import com.github.vincentj711.kotlinorganizer.services.MyProjectService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

internal class MyProjectManagerListener : ProjectManagerListener {
  override fun projectOpened(project: Project) {
    project.getService(MyProjectService::class.java)
  }
}
