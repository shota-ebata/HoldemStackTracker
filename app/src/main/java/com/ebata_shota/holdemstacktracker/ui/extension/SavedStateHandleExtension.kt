@file:Suppress("UNCHECKED_CAST")

package com.ebata_shota.holdemstacktracker.ui.extension

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadOnlyProperty

fun <T : ViewModel, V> SavedStateHandle.param(key: String? = null) = ReadOnlyProperty<T, V> { thisRef, property ->
    val paramKey = key ?: property.name
    this@param.get<V>(paramKey) as V
}