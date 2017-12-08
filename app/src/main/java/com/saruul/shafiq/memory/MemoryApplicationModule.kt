package com.saruul.shafiq.memory

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MemoryApplicationModule {

    @ContributesAndroidInjector
    abstract fun contributeActivityInjector(): MainActivity
}
