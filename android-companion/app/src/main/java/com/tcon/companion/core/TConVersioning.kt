package com.tcon.companion.core

object TConVersioning {
    private val pattern = Regex("(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)")

    fun isValid(version: String): Boolean = pattern.matches(version)

    fun versionCodeFor(version: String): Long {
        require(isValid(version)) { "Versi rilis tidak valid." }
        val parts = version.split('.').map { it.toLong() }
        require(parts[0] <= 2147 && parts[1] <= 999 && parts[2] <= 999) {
            "Versi rilis berada di luar rentang versionCode TCon."
        }
        val code = parts[0] * 1_000_000L + parts[1] * 1_000L + parts[2]
        require(code in 2L..2_100_000_000L) {
            "Versi rilis berada di luar rentang versionCode Android."
        }
        return code
    }

    fun compare(left: String, right: String): Int {
        require(isValid(left) && isValid(right)) { "Versi yang dibandingkan tidak valid." }
        val a = left.split('.').map { it.toLong() }
        val b = right.split('.').map { it.toLong() }
        for (index in 0..2) {
            val difference = a[index].compareTo(b[index])
            if (difference != 0) return difference
        }
        return 0
    }
}
