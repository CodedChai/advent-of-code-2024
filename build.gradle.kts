plugins {
  kotlin("jvm") version "2.1.0"
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
  implementation("io.arrow-kt:arrow-core:1.2.4")
  implementation("io.arrow-kt:arrow-fx-coroutines:1.2.4")
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
