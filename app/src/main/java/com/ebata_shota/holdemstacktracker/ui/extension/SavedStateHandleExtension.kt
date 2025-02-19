@file:Suppress("UNCHECKED_CAST")

package com.ebata_shota.holdemstacktracker.ui.extension

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T : ViewModel, V> SavedStateHandle.param(key: String? = null) = ReadOnlyProperty<T, V> { thisRef, property ->
    val paramKey = key ?: property.name
    this@param.get<V>(paramKey) as V
}

inline fun <reified T: ViewModel, reified V> SavedStateHandle.state(key: String? = null) = object : ReadWriteProperty<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        val paramKey = key ?: property.name
        return this@state.get<V>(paramKey) as V
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        val paramKey = key ?: property.name
        this@state[paramKey] = value
    }
}