plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "av.staz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val os = org.gradle.internal.os.OperatingSystem.current()
val platform = when {
    os.isWindows -> "win"
    os.isMacOsX -> "mac"
    os.isLinux -> "linux"
    else -> throw GradleException("Unsupported OS")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("org.openjfx:javafx-controls:21:$platform")
    implementation("org.openjfx:javafx-graphics:21:$platform")
    implementation("org.openjfx:javafx-fxml:21:$platform")
    implementation("org.openjfx:javafx-base:21:$platform")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("av.staz.SkrzyzowanieApp")
    val javafxLibs = configurations.runtimeClasspath.get()
        .filter { it.name.contains("javafx") }
        .joinToString(File.pathSeparator) { it.absolutePath }
    applicationDefaultJvmArgs = listOf(
        "--module-path", javafxLibs,
        "--add-modules", "javafx.controls,javafx.fxml"
    )
}