plugins {
  kotlin("jvm") version "2.1.0"
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

repositories {
  mavenCentral()
}

sourceSets {
  main {
    kotlin.srcDir("src")
  }
}

tasks {
  wrapper {
    gradleVersion = "8.11.1"
  }
}
