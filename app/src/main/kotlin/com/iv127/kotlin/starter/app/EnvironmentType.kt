package com.iv127.kotlin.starter.app

enum class EnvironmentType(val shortName: String) {
    LOCAL("local"),
    PRODUCTION("prod"),
    TEST("test");

    companion object {
        private val ENVIRONMENT_TYPES_BY_SHORT_NAME: Map<String, EnvironmentType> =
            entries.associateBy { type -> type.shortName }

        fun parseShortName(shortName: String): EnvironmentType? {
            return ENVIRONMENT_TYPES_BY_SHORT_NAME[shortName]
        }
    }
}
