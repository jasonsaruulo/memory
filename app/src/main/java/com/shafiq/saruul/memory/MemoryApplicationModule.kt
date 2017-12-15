package com.shafiq.saruul.memory

import com.shafiq.saruul.memory.main.MainActivity
import com.shafiq.saruul.memory.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MemoryApplicationModule {

    @ContributesAndroidInjector(modules = arrayOf(MainModule::class))
    abstract fun contributeMainActivityInjector(): MainActivity
}
