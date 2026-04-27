package com.example.pcraft.di

import android.content.Context
import com.example.pcraft.data.dao.BuildDao
import com.example.pcraft.data.dao.ComponentDao
import com.example.pcraft.data.repository.AuthRepository
import com.example.pcraft.data.repository.BuildRepository
import com.example.pcraft.data.repository.ComponentRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        firestore: FirebaseFirestore?
    ) = AuthRepository(context, firestore)

    @Provides
    @Singleton
    fun provideComponentRepository(
        componentDao: ComponentDao,
        @ApplicationContext context: Context,
        authRepository: AuthRepository,
        firestore: FirebaseFirestore?
    ) = ComponentRepository(componentDao, context, authRepository, firestore)

    @Provides
    @Singleton
    fun provideBuildRepository(
        buildDao: BuildDao,
        @ApplicationContext context: Context,
        authRepository: AuthRepository,
        firestore: FirebaseFirestore?
    ) = BuildRepository(buildDao, context, authRepository, firestore)
}
