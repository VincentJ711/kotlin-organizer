import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.3.72"
  id("org.jetbrains.intellij") version "0.4.21"
}

repositories {
  jcenter()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

intellij {
  version = "2019.3"
  updateSinceUntilBuild = false
  setPlugins("java", "org.jetbrains.kotlin:1.3.72-release-IJ2019.3-1")
}

tasks {
  // Set the compatibility versions to 1.8
  withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
  }
  listOf("compileKotlin", "compileTestKotlin").forEach {
    getByName<KotlinCompile>(it) {
      kotlinOptions.jvmTarget = "1.8"
    }
  }

  test {
    useJUnitPlatform()
  }
}