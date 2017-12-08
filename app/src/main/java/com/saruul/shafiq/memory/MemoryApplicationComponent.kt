package com.saruul.shafiq.memory

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(modules = arrayOf(AndroidInjectionModule::class, MemoryApplicationModule::class))
interface MemoryApplicationComponent : AndroidInjector<MemoryApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): MemoryApplicationComponent.Builder

        fun build(): MemoryApplicationComponent
    }
}
