package com.shafiq.saruul.memory

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class, MemoryApplicationModule::class))
interface MemoryApplicationComponent : AndroidInjector<MemoryApplication>
