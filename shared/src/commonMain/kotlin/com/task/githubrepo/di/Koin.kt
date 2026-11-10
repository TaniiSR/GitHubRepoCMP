package com.task.githubrepo.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/** Provides the platform's HttpClientEngine and SqlDriver. */
expect fun platformModule(): Module

fun initKoin(extra: KoinAppDeclaration? = null): KoinApplication = startKoin {
    extra?.invoke(this)
    modules(sharedModule, platformModule())
}
