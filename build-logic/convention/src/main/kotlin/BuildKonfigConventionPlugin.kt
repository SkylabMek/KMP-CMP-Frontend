import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class BuildKonfigConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.codingfeline.buildkonfig")

            /**
             * Determine Environment and Build Type.
             *
             * You can override these via Gradle properties:
             * -Penv=production or -Penv=dev
             * -PbuildType=release or -PbuildType=debug
             *
             * By default, it detects production based on task names (e.g. Release, Distribution).
             */
            val envProperty = providers.gradleProperty("env").getOrElse("dev").lowercase()
            val taskNames = gradle.startParameter.taskNames

            val isProduction = envProperty == "prod" || envProperty == "production" ||
                    taskNames.any {
                        it.contains("Release", ignoreCase = true) ||
                                it.contains("Distribution", ignoreCase = true) ||
                                it.contains("bundle", ignoreCase = true)
                    }

            val buildTypeProperty = providers.gradleProperty("buildType").getOrElse("debug").lowercase()
            
            // isDebug is true only if explicitly requested or if it's a dev task (not Release/Distribution/Bundle)
            val isDebug = buildTypeProperty == "debug" &&
                    taskNames.none { 
                        it.contains("Release", ignoreCase = true) ||
                        it.contains("Distribution", ignoreCase = true) ||
                        it.contains("bundle", ignoreCase = true)
                    }

            extensions.configure<BuildKonfigExtension> {
                packageName = "th.skylabmek.kmp_frontend.core.common"
                objectName = "AppBuildKonfig"

                // Base values
                val prodBaseUrl = "https://api.skylabmek.net/"
                val devBaseUrl = "http://localhost:3000"
                val devAndroidBaseUrl = "http://10.0.2.2:3000"
                val brandfetchClientId = "1idi9KbP-wRzPrDwdwW"

                // Default Configuration (Visible in commonMain)
                defaultConfigs {
                    buildConfigField(Type.BOOLEAN, "IS_PRODUCTION", isProduction.toString())
                    buildConfigField(Type.BOOLEAN, "IS_DEBUG", isDebug.toString())
                    buildConfigField(Type.STRING, "PROFILE_ID", "profile_001")
                    buildConfigField(Type.STRING, "APP_ID", "website_main_001")
                    buildConfigField(Type.STRING, "BASE_URL", if (isProduction) prodBaseUrl else devBaseUrl)
                    buildConfigField(Type.STRING, "BRANDFETCH_CLIENT_ID", brandfetchClientId)
                }

                // Target-specific overrides
                targetConfigs {
                    create("android") {
                        buildConfigField(Type.STRING, "BASE_URL", if (isProduction) prodBaseUrl else devAndroidBaseUrl)
                    }
                }

                // Variants/Flavors
                defaultConfigs("dev") {
                    buildConfigField(Type.BOOLEAN, "IS_PRODUCTION", "false")
                    buildConfigField(Type.BOOLEAN, "IS_DEBUG", "true")
                    buildConfigField(Type.STRING, "PROFILE_ID", "profile_001")
                    buildConfigField(Type.STRING, "APP_ID", "website_main_001")
                    buildConfigField(Type.STRING, "BASE_URL", devBaseUrl)
                    buildConfigField(Type.STRING, "BRANDFETCH_CLIENT_ID", brandfetchClientId)
                }

                defaultConfigs("production") {
                    buildConfigField(Type.BOOLEAN, "IS_PRODUCTION", "true")
                    buildConfigField(Type.BOOLEAN, "IS_DEBUG", "false")
                    buildConfigField(Type.STRING, "PROFILE_ID", "profile_001")
                    buildConfigField(Type.STRING, "APP_ID", "website_main_001")
                    buildConfigField(Type.STRING, "BASE_URL", prodBaseUrl)
                    buildConfigField(Type.STRING, "BRANDFETCH_CLIENT_ID", brandfetchClientId)
                }
            }
        }
    }
}
