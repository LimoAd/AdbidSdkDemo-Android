pluginManagement {
    repositories {
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.leadmoad.com/repository/maven-releases/")
        }
        maven { url = uri("https://developer.huawei.com/repo") }
        maven { url = uri("https://developer.hihonor.com/repo") }

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.leadmoad.com/repository/maven-releases/")
        }
        maven { url = uri("https://developer.huawei.com/repo") }
        maven { url = uri("https://developer.hihonor.com/repo") }


        google()
        mavenCentral()

    }
}

rootProject.name = "AdBid"
include(":app")
