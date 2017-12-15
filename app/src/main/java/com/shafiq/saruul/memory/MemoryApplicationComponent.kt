package com.shafiq.saruul.memory

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(modules = arrayOf(AndroidInjectionModule::class, MemoryApplicationModule::class))
interface MemoryApplicationComponent : AndroidInjector<MemoryApplication>
