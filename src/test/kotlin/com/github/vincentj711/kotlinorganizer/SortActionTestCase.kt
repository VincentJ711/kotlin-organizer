package com.github.vincentj711.kotlinorganizer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.KotlinFileType

class SortActionTestCase(
    private val config: Config,
    private val inputFile: String,
    private val expectedOutputFile: String
) : LightJavaCodeInsightFixtureTestCase() {
  init {
    setUp()
  }

  fun execute() {
    val action = SortAction(config)
    myFixture.configureByText(KotlinFileType.INSTANCE, inputFile)
    ApplicationManager.getApplication()
        .invokeAndWait { myFixture.testAction(action) }
    val outputFile = myFixture.file.text

    try {
      assertEquals(simplify(expectedOutputFile), simplify(outputFile))
      tearDown()
    } catch (e: Throwable) {
      println(outputFile)
      tearDown()
      throw e
    }
  }

  private fun simplify(text: String) =
      text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
}