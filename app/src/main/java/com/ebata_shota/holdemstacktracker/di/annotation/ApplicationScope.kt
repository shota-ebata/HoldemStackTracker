package com.ebata_shota.holdemstacktracker.di.annotation

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME) // TODO: RUNTIME or BINARY をちゃんと調べてやる
annotation class ApplicationScope