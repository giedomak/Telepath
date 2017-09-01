/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.utilities

/**
 * Log messages with the following five levels: debug, info, warning, error and fatal.
 */
object Logger {

    private var padding = 20

    fun debug(message: Any, println: Boolean = true) {
        log(message.toString(), println)
    }

    fun info(message: Any) {
        log(message.toString())
    }

    fun warning(message: Any) {
        log(message.toString())
    }

    fun error(message: Any) {
        log(message.toString())
    }

    fun fatal(message: Any) {
        log(message.toString())
    }

    /**
     * This wil log a message to the console.
     *
     * @param message The message to print.
     * @param println Boolean indicating if we want to print on a newline or not.
     */
    private fun log(message: String, println: Boolean = true) {

        // Dynamically adjust our padding length
        if (callerClassName.length > padding) padding = callerClassName.length

        if (println) {
            println(padLeft(callerClassName, padding) + ": " + message)
        } else {
            print(padLeft(callerClassName, padding) + ": " + message)
        }
    }

    /**
     * Get the Caller's ClassName, so we can add that in our log.
     *
     * @return A string with the ClassName of the caller, with the packages stripped away.
     */
    private val callerClassName: String
        get() {
            val stElements = Thread.currentThread().stackTrace
            for (i in 1..stElements.size - 1) {
                val ste = stElements[i]
                val name = ste.className
                if (name != Logger::class.java.name &&
                        name.indexOf("java.lang.Thread") != 0 &&
                        !name.contains("Lambda") &&
                        !name.contains("ArrayList") &&
                        !name.contains("ForEachOps") &&
                        !name.contains("SliceOps") &&
                        !name.contains("Pipeline")) {
                    return name.substring(name.lastIndexOf('.') + 1)
                }
            }
            return ""
        }

    /**
     * Pad a String with spaces from the left.
     *
     * @param message The String to pad
     * @param n       Number of total chars
     * @return The padded String
     */
    private fun padLeft(message: String, n: Int): String {
        return String.format("%1$" + n + "s", message)
    }
}
