package com.letrix.anime.utils

import timber.log.Timber

class TimberTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return "(Timber) [L:${element.lineNumber}] [M:${element.methodName}] [C:${
            super.createStackElementTag(
                element
            )
        }]"
    }
}