package org.fossify.clock.extensions

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    this[index1] = this[index2].also {
        this[index2] = this[index1]
    }
}
