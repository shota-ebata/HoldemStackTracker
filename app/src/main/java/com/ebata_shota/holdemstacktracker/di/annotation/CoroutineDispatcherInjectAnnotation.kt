package com.ebata_shota.holdemstacktracker.di.annotation

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoroutineDispatcherIO

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoroutineDispatcherDefault

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoroutineDispatcherMain
