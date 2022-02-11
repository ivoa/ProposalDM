
plugins {
        id("net.ivoa.vo-dml.vodmltools") version "0.2.3"
}

group = "org.javastro.ivoa.dm"
version = "0.2-SNAPSHOT"


vodml {
        vodmlFiles.setFrom(project.files (
                vodmlDir.file("proposaldm.vo-dml.xml"),
                vodmlDir.file("proposalManagement.vo-dml.xml"),
                vodmlDir.file("IVOA-v1.0.vo-dml.xml"),
                vodmlDir.file("STC_coords-v1.0.vo-dml.xml")
        ))
        bindingFiles.setFrom(
                project.files(
                        layout.projectDirectory.asFileTree.matching (
                                PatternSet().include("binding*model.xml")
                        )
                )
        )
        outputDocDir.set(layout.projectDirectory.dir("std/generated"))

        modelsToDocument.set("proposal,proposalManagement")
}

java {
        toolchain {
                languageVersion.set(JavaLanguageVersion.of(8)) // make it explicit that we are still at 1.8
        }
}

repositories {
        mavenCentral()
        mavenLocal() // TODO remove this when releasing - just here to pick up local vodml-runtime
}



tasks.test {
        useJUnitPlatform()
}


dependencies {
//    implementation("org.javastro:ivoa-entities:0.9.3-SNAPSHOT")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

        implementation("org.slf4j:slf4j-api:1.7.32")
        testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")

        testImplementation("org.apache.derby:derby:10.14.2.0")
        testImplementation("org.javastro:jaxbjpa-utils:0.1.1")
        testImplementation("org.javastro:jaxbjpa-utils:0.1.1:test")

}