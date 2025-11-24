// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false // Use the direct alias from libs.versions.toml
    alias(libs.plugins.kotlinCompose) apply false // Use the direct alias from libs.versions.toml
    alias(libs.plugins.ksp) apply false
}
