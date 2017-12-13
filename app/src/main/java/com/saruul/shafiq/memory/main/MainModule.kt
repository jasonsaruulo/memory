package com.saruul.shafiq.memory.main

import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import java.util.Random

@Module
class MainModule {

    @Provides
    fun provideRandom(): Random {
        return Random()
    }

    @Provides
    fun providePicasso(mainActivity: MainActivity): Picasso {
        return Picasso.with(mainActivity)
    }

    @Provides
    fun provideIntegerSet(): MutableSet<Int> {
        return mutableSetOf()
    }
}
