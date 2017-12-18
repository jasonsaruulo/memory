package com.shafiq.saruul.memory.main

import com.shafiq.saruul.memory.ActivityScoped
import com.shafiq.saruul.memory.FragmentScoped
import com.shafiq.saruul.memory.handlers.MemoryPermissionHandler
import com.shafiq.saruul.memory.handlers.MemoryStorageHandler
import com.shafiq.saruul.memory.handlers.PermissionHandler
import com.shafiq.saruul.memory.handlers.StorageHandler
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import java.util.*

@Module
abstract class MainModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideRandom(): Random {
            return Random()
        }

        @JvmStatic
        @Provides
        fun providePicasso(mainActivity: MainActivity): Picasso {
            return Picasso.with(mainActivity)
        }

        @JvmStatic
        @Provides
        fun provideStringList(): MutableList<String> {
            return mutableListOf()
        }

        @JvmStatic
        @Provides
        fun provideStringPicassoTargetMap(): MutableMap<String, Target> {
            return mutableMapOf()
        }

        @JvmStatic
        @Provides
        fun provideIntList(): MutableList<Int> {
            return mutableListOf()
        }

        @JvmStatic
        @Provides
        fun provideMemoryCardViewList(): MutableList<MemoryCardView> {
            return mutableListOf()
        }
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ActivityScoped
    @Binds
    abstract fun bindMainPresenter(mainPresenter: MainPresenter): MainContract.Presenter

    @ActivityScoped
    @Binds
    abstract fun bindPermissionHandler(permissionHandler: MemoryPermissionHandler):
            PermissionHandler

    @ActivityScoped
    @Binds
    abstract fun bindStorageHandler(storageHandler: MemoryStorageHandler): StorageHandler
}
