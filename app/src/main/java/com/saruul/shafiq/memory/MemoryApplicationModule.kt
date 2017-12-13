package com.saruul.shafiq.memory

import com.saruul.shafiq.memory.main.MainActivity
import com.saruul.shafiq.memory.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MemoryApplicationModule {

    @ContributesAndroidInjector(modules = arrayOf(MainModule::class))
    abstract fun contributeMainActivityInjector(): MainActivity
}
