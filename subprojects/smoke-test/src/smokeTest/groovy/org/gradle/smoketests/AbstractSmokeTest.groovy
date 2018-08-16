/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.smoketests

import org.apache.commons.io.FileUtils
import org.gradle.integtests.fixtures.RepoScriptBlockUtil
import org.gradle.integtests.fixtures.executer.IntegrationTestBuildContext
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.api.internal.artifacts.BaseRepositoryFactory.PLUGIN_PORTAL_OVERRIDE_URL_PROPERTY
import static org.gradle.integtests.fixtures.RepoScriptBlockUtil.createMirrorInitScript
import static org.gradle.integtests.fixtures.RepoScriptBlockUtil.gradlePluginRepositoryMirrorUrl

abstract class AbstractSmokeTest extends Specification {

    static class TestedVersions {
        /**
         * May also need to update
         * @see BuildScanPluginSmokeTest
         */

        // https://plugins.gradle.org/plugin/nebula.dependency-recommender
        static nebulaDependencyRecommender = "6.1.1"

        // https://plugins.gradle.org/plugin/nebula.plugin-plugin
        static nebulaPluginPlugin = "7.1.9"

        // https://plugins.gradle.org/plugin/nebula.lint
        static nebulaLint = "9.3.4"

        // https://plugins.gradle.org/plugin/nebula.dependency-lock
        static nebulaDependencyLock = Versions.of("4.9.5", "5.0.6", "6.0.0")

        // https://plugins.gradle.org/plugin/nebula.resolution-rules
        static nebulaResolutionRules = "6.0.1"

        // https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow
        static shadow = Versions.of("2.0.4")

        // https://github.com/asciidoctor/asciidoctor-gradle-plugin/releases
        static asciidoctor = "1.5.8"

        // https://plugins.gradle.org/plugin/com.bmuschko.docker-java-application
        static docker = "3.5.0"

        // https://plugins.gradle.org/plugin/com.bmuschko.tomcat
        static tomcat = "2.5"

        // https://plugins.gradle.org/plugin/io.spring.dependency-management
        static springDependencyManagement = "1.0.6.RELEASE"

        // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-gradle-plugin
        static springBoot = "2.0.4.RELEASE"

        // https://developer.android.com/studio/releases/platform-tools
        static androidTools = "27.0.3"
        // https://mvnrepository.com/artifact/com.android.tools.build/gradle
        static androidGradle3x = "3.1.4"
        static androidGradle = Versions.of(androidGradle3x)

        // https://blog.jetbrains.com/kotlin/
        static kotlin = Versions.of('1.2.21', '1.2.31', '1.2.41', '1.2.51')

        // https://plugins.gradle.org/plugin/org.gretty
        static gretty = "2.2.0"

        // https://plugins.gradle.org/plugin/com.eriwen.gradle.js
        static gradleJs = "2.14.1"

        // https://plugins.gradle.org/plugin/com.eriwen.gradle.css
        static gradleCss = "2.14.0"

        // https://plugins.gradle.org/plugin/org.gosu-lang.gosu
        static gosu = "0.3.10"

        // https://plugins.gradle.org/plugin/org.xtext.xtend
        static xtend = "1.0.21"
        static grgit = "3.0.0-beta.1"
    }

    static class Versions implements Iterable<String> {
        static Versions of(String... versions) {
            new Versions(versions)
        }

        final List<String> versions

        String latest() {
            versions.last()
        }

        private Versions(String... given) {
            versions = Arrays.asList(given)
        }

        @Override
        Iterator<String> iterator() {
            return versions.iterator()
        }
    }

    private static final String INIT_SCRIPT_LOCATION = "org.gradle.smoketests.init.script"

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = new File(testProjectDir.root, defaultBuildFileName)
    }

    protected String getDefaultBuildFileName() {
        'build.gradle'
    }

    File file(String filename) {
        def file = new File(testProjectDir.root, filename)
        def parentDir = file.getParentFile()
        assert parentDir.isDirectory() || parentDir.mkdirs()

        file
    }

    GradleRunner runner(String... tasks) {
        GradleRunner.create()
            .withGradleInstallation(IntegrationTestBuildContext.INSTANCE.gradleHomeDir)
            .withTestKitDir(IntegrationTestBuildContext.INSTANCE.gradleUserHomeDir)
            .withProjectDir(testProjectDir.root)
            .withArguments(tasks.toList() + ['-s'] + repoMirrorParameters())
    }

    private List<String> repoMirrorParameters() {
        String mirrorInitScriptPath = createMirrorInitScript().absolutePath
        return ['-I', mirrorInitScriptPath, "-D${PLUGIN_PORTAL_OVERRIDE_URL_PROPERTY}=${gradlePluginRepositoryMirrorUrl()}".toString(), "-D${INIT_SCRIPT_LOCATION}=${mirrorInitScriptPath}".toString()]
    }

    protected void useSample(String sampleDirectory) {
        def smokeTestDirectory = new File(this.getClass().getResource(sampleDirectory).toURI())
        FileUtils.copyDirectory(smokeTestDirectory, testProjectDir.root)
    }

    protected void replaceVariablesInBuildFile(Map binding) {
        String text = buildFile.text
        binding.each { String var, String value ->
            text = text.replaceAll("\\\$${var}".toString(), value)
        }
        buildFile.text = text
    }

    protected static String jcenterRepository() {
        RepoScriptBlockUtil.jcenterRepository()
    }

    protected static String mavenCentralRepository() {
        RepoScriptBlockUtil.mavenCentralRepository()
    }

    protected static String googleRepository() {
        RepoScriptBlockUtil.googleRepository()
    }
}
