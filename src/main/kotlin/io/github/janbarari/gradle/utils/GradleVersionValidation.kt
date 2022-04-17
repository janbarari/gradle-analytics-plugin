package io.github.janbarari.gradle.utils

import org.gradle.util.GradleVersion

class GradleVersionValidation {

    companion object {

        enum class GradleVersions(val versionNumber: String) {
            v7_4_2("7.4.2"),
            v7_4_1("7.4.1"),
            v7_4("7.4"),

            v7_3_3("7.3.3"),
            v7_3_2("7.3.2"),
            v7_3_1("7.3.1"),
            v7_3("7.3"),

            v7_2("7.2"),

            v7_1_1("7.1.1"),
            v7_1("7.1"),

            v7_0_2("7.0.2"),
            v7_0_1("7.0.1"),
            v7_0("7.0"),

            v6_9_2("6.9.2"),
            v6_9_1("6.9.1"),
            v6_9("6.9"),

            v6_8_3("6.8.3"),
            v6_8_2("6.8.2"),
            v6_8_1("6.8.1"),
            v6_8("6.8"),

            v6_7_1("6.7.1"),
            v6_7("6.7"),

            v6_6_1("6.6.1"),
            v6_6("6.6"),

            v6_5_1("6.5.1"),
            v6_5("6.5"),

            v6_4_1("6.4.1"),
            v6_4("6.4"),

            v6_3("6.3"),

            v6_2_2("6.2.2"),
            v6_2_1("6.2.1"),
            v6_2("6.2"),

            v6_1_1("6.1.1"),
            v6_1("6.1")
        }

        fun isGradleCompatibleWith(version: GradleVersions): Boolean {
            val currentGradleVersion = GradleVersion.current()
            val minimumRequiredGradleVersion = GradleVersion.version(version.versionNumber)
            return currentGradleVersion > minimumRequiredGradleVersion
        }

    }

}
