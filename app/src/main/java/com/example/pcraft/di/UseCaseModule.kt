package com.example.pcraft.di

import com.example.pcraft.domain.usecase.CheckCompatibilityUseCase
import com.example.pcraft.domain.usecase.CalculateBuildPriceUseCase
import com.example.pcraft.domain.usecase.CalculatePsuLoadUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideCheckCompatibilityUseCase() = CheckCompatibilityUseCase()

    @Provides
    fun provideCalculateBuildPriceUseCase() = CalculateBuildPriceUseCase()

    @Provides
    fun provideCalculatePsuLoadUseCase() = CalculatePsuLoadUseCase()
}
