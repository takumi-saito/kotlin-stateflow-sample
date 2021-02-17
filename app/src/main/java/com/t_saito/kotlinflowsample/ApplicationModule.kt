package com.t_saito.kotlinflowsample

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Provides
    @Singleton
    fun provideMoshiUserAdapter(): JsonAdapter<User> {
        return Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(User::class.java, "type")
                    .withSubtype(User.Normal::class.java, "normal")
                    .withSubtype(User.Special::class.java, "special")

            )
            .build()
            .adapter(User::class.java)
    }
}