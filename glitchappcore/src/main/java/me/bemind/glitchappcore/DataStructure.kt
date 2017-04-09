package me.bemind.glitchappcore

import java.util.*

/**
 * Created by angelomoroni on 09/04/17.
 */

interface Stack<T> : Iterable<T> {

    fun push(item: T)

    fun pop(): T

    fun peek(): T

    fun isEmpty(): Boolean {
        return size() == 0
    }

    fun size(): Int

    fun clear()

    fun clearManteningFirst()
}

class LinkedStack<T> :Stack<T>{
    val stack =  LinkedList<T>()

    override fun push(item: T) {
        stack.add(item)
    }

    override fun pop(): T {
        return stack.removeLast()
    }

    override fun peek(): T {
        return stack.last
    }

    override fun size(): Int {
        return stack.size
    }

    override fun clear() {
        stack.clear()
    }

    override fun clearManteningFirst() {
        val  t = stack.first
        stack.clear()
        push(t)
    }

    override fun iterator(): Iterator<T> {
        return stack.iterator()
    }

    fun first():T = stack.first

    fun addAll(collection: Collection<T> = ArrayList<T>()) = stack.addAll(collection)

    fun getAllAsList() :ArrayList<T> = ArrayList(stack)

}