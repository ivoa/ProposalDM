import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo

plugins {
        id("net.ivoa.vo-dml.vodmltools") version "0.3.22"
        `maven-publish`
        id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
        signing
}

group = "org.javastro.ivoa.dm"
version = "0.3.3-SNAPSHOT"


vodml {
        vodmlFiles.setFrom(project.files (
                vodmlDir.file("proposaldm.vo-dml.xml"),
                vodmlDir.file("proposalManagement.vo-dml.xml"),
 //               vodmlDir.file("IVOA-v1.0.vo-dml.xml"),
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
        outputSiteDir.set(layout.projectDirectory.dir("docs/generated"))

        modelsToDocument.set("proposal,proposalManagement")
}

/* uncomment this if not using the eclipse vodsl plugin to edit the vodsl
*  or just run the vodslToVodml task manually as necessary */
//tasks.named("vodmlJavaGenerate") {
//        dependsOn("vodslToVodml")
//}

java {
        toolchain {
                languageVersion.set(JavaLanguageVersion.of(11)) // moved to Java 11
        }
        withJavadocJar()
        withSourcesJar()
}



//make the fact that sources are generated explicit (gets rid of warning that it will not work in gradle 8)- see https://melix.github.io/blog/2021/10/gradle-quickie-dependson.html
tasks.named<Jar>("sourcesJar") {
        from(tasks.named("vodmlGenerateJava"))
}




tasks.test {
        useJUnitPlatform()
}

//create jar with the test classes in - this is added as artifact to maven publication below, so automatically created
val tjar = tasks.register<Jar>("testJar") {
        from(sourceSets.test.get().output)
        archiveClassifier.set("test")
}
val pjar = tasks.register<Jar>("JarWithoutPersistence") {
        from(sourceSets.main.get().output)
        archiveClassifier.set("quarkus")
        exclude("META-INF/persistence.xml")
}

tasks.register<Copy>("copyJavaDocForSite") {
        from(layout.buildDirectory.dir("docs/javadoc"))
        into(vodml.outputSiteDir.dir("javadoc"))
        dependsOn(tasks.javadoc)

}
tasks.register<Exec>("testSite"){
        commandLine("mkdocs", "serve")
        dependsOn("vodmlSite")
        dependsOn("copyJavaDocForSite")
}
tasks.register<Exec>("doSite"){
        commandLine("mkdocs", "gh-deploy", "--force")
        dependsOn("vodmlSite")
        dependsOn("copyJavaDocForSite")
}

tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE } //IMPL bugfix - see https://stackoverflow.com/questions/67265308/gradle-entry-classpath-is-a-duplicate-but-no-duplicate-handling-strategy-has-b

dependencies {
        api("org.javastro.ivoa.vo-dml:ivoa-base:1.1-SNAPSHOT")
//    implementation("org.javastro:ivoa-entities:0.9.3-SNAPSHOT")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

        implementation("org.slf4j:slf4j-api:1.7.32")
        testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")

        testImplementation("com.h2database:h2:2.1.214") // try out h2
//        testImplementation("org.apache.derby:derby:10.14.2.0")
        testImplementation("org.javastro:jaxbjpa-utils:0.1.2")

        testRuntimeOnly("org.postgresql:postgresql:42.3.3")
}

tasks.register<Exec>("createTestData")
{
        dependsOn(tasks.build)
        description = "creates test data in local postgres database"
        commandLine = listOf("java","-classpath",sourceSets.test.get().runtimeClasspath.asPath,"org.ivoa.dm.proposal.prop.DataGenerator")
// run pg_dump -a vodml_proposal -f testData.sql to extract the data
}


publishing {
        publications {
                create<MavenPublication>("mavenJava") {
                        from(components["java"])
                        artifact(tjar)
                        artifact(pjar)
                        versionMapping {
                                usage("java-api") {
                                        fromResolutionOf("runtimeClasspath")
                                }
                                usage("java-runtime") {
                                        fromResolutionResult()
                                }
                        }
                        pom {
                                name.set("Proposal Data Model")
                                description.set("Code generated from the IVOA ProposalDM VO-DML")
                                url.set("https://www.ivoa.net/documents/ProposalDM/")
                                licenses {
                                        license {
                                                name.set("The Apache License, Version 2.0")
                                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                        }
                                }
                                developers {
                                        developer {
                                                id.set("pahjbo")
                                                name.set("Paul Harrison")
                                                email.set("paul.harrison@manchester.ac.uk")
                                        }
                                }
                                scm {
                                        connection.set("scm:git:git://github.com/ivoa/ProposalDM.git")
                                        developerConnection.set("scm:git:ssh://github.com/ivoa/ProposalDM.git")
                                        url.set("https://github.com/ivoa/ProposalDM")
                                }
                        }
                }
        }
}
nexusPublishing {
        repositories {
                sonatype()
        }
}

//do not generate extra load on Nexus with new staging repository if signing fails
tasks.withType<io.github.gradlenexus.publishplugin.InitializeNexusStagingRepository>().configureEach{
        shouldRunAfter(tasks.withType<Sign>())
}

signing {
        setRequired { !project.version.toString().endsWith("-SNAPSHOT") && !project.hasProperty("skipSigning") }

        if (!project.hasProperty("skipSigning")) {
                useGpgCmd()
                sign(publishing.publications["mavenJava"])
        }
}
