package com.github.vincentj711.kotlinorganizer.services

import com.intellij.openapi.project.Project
import com.github.vincentj711.kotlinorganizer.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
