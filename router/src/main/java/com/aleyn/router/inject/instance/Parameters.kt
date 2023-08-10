package com.aleyn.router.inject.instance

import com.aleyn.router.inject.getFullName
import kotlin.reflect.KClass

/**
 * @author: Aleyn
 * @date: 2023/7/27 11:08
 */
@Suppress("UNCHECKED_CAST")
class Parameters(
    private val _values: MutableList<Any?> = mutableListOf()
) {

    val values: List<Any?> get() = _values

    private var index: Int? = null

    /**
     * get element at given index
     * return T
     */
    operator fun <T> get(i: Int) = _values[i] as T

    fun <T> set(i: Int, t: T) {
        _values[i] = t as Any
    }

    fun insert(index: Int, value: Any) = apply {
        _values.add(index, value)
    }

    fun add(value: Any) = apply {
        _values.add(value)
    }

    /**
     * Get first element of given type T
     * return T
     */
    inline fun <reified T : Any> get(): T =
        getOrNull(T::class)
            ?: throw RuntimeException("No value found for type '${T::class.getFullName()}'")

    /**
     * Get first element of given type T
     * return T
     */
    fun <T> getOrNull(clazz: KClass<*>): T? {
        return if (_values.isEmpty()) null
        else {
            increaseIndex()
            val currentValue: T? =
                _values[index!!]?.let { if (clazz.isInstance(it)) it as? T? else null }
            if (currentValue == null) {
                restoreIndex()
            }
            currentValue
        }
    }

    @PublishedApi
    internal fun increaseIndex() {
        val newIndex = index.let { index ->
            when {
                index == null -> 0
                index < _values.lastIndex -> index + 1
                else -> _values.lastIndex
            }
        }
        index = newIndex
    }

    @PublishedApi
    internal fun restoreIndex() {
        val newIndex = index?.let { index ->
            when {
                index == 0 -> null
                index > 0 -> index - 1
                else -> 0
            }
        }
        index = newIndex
    }

    /**
     * Get first element of given type T
     * return T
     */
    inline fun <reified T> getOrNull(): T? = getOrNull(T::class)

    override fun toString(): String = "DefinitionParameters${_values.toList()}"
}