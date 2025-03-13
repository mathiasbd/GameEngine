plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"
val lwjglVersion = "3.3.6"
val jomlVersion = "1.10.7"
val lwjglNatives = listOf(
    "natives-windows",
    "natives-macos",
    "natives-macos-arm64"
)
repositories {
    mavenCentral()
}
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.joml", "joml", jomlVersion)
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")

    lwjglNatives.forEach { native ->
        runtimeOnly("org.lwjgl", "lwjgl", classifier = native)
        runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = native)
        runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = native)
        runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = native)
        runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = native)
    }
}

tasks.test {
    useJUnitPlatform()
}
application {
    mainClass.set("org.example.Main")
}
tasks.withType<JavaExec> {
    jvmArgs = listOf("-XstartOnFirstThread")
}