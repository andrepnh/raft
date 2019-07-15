plugins {
    java
    application
}

group = "am.consensus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    create("featureTest") {
        java {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
            srcDir("src/feature-test/java")
        }
        resources.srcDir("src/feature-test/resources")
    }
}

val featureTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
val featureTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.testRuntime.get())
}

dependencies {
    compile("com.google.guava:guava:28.0-jre")
    compile("com.google.inject:guice:4.2.2")
    compile("com.google.inject.extensions:guice-assistedinject:4.2.2")

    compile("com.lmax:disruptor:3.4.2") // async logging
    compile("org.fusesource.jansi:jansi:1.18") // log colors for windows; see log4j2 config XMLs
    compile("org.apache.logging.log4j:log4j-api:2.12.0")
    compile("org.apache.logging.log4j:log4j-core:2.12.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.assertj:assertj-core:3.12.2")

    // Cucumber doesn't have full-fledged support for junit 5 at the moment
    featureTestImplementation("junit:junit:4.12")
    featureTestImplementation("io.cucumber:cucumber-java:4.5.3")
    featureTestImplementation("io.cucumber:cucumber-junit:4.5.3")
    featureTestImplementation("io.cucumber:cucumber-guice:4.5.3")
    featureTestImplementation("org.assertj:assertj-core:3.12.2")
}

application {
    mainClassName = "am.raft.Bootstrapper"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "FAILED")
    }
}

val featureTest = task<Test>("featureTest") {
    useJUnit()
    description = "Runs feature tests."
    group = "verification"
    testClassesDirs = sourceSets["featureTest"].output.classesDirs
    classpath = sourceSets["featureTest"].runtimeClasspath

    testLogging {
        events("PASSED", "FAILED")
        showStandardStreams = true
    }
    systemProperty("log4j.skipJansi", "false") // Color highlighting on Windows
    // Async logging
//    systemProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector")

    shouldRunAfter("test")
}

tasks.check { dependsOn(featureTest) }