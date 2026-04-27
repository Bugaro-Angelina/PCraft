package com.example.pcraft.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcraft.data.model.BuildConfiguration
import com.example.pcraft.data.model.Component
import com.example.pcraft.data.repository.BuildRepository
import com.example.pcraft.data.repository.ComponentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: ComponentRepository
) : ViewModel() {

    val favoriteComponents: Flow<List<Component>> = repository.favoriteComponents

    fun toggleFavorite(component: Component) {
        viewModelScope.launch {
            repository.toggleFavorite(component)
        }
    }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val buildRepository: BuildRepository
) : ViewModel() {

    val builds: Flow<List<BuildConfiguration>> = buildRepository.allBuilds

    fun deleteBuild(buildId: String) {
        viewModelScope.launch {
            buildRepository.deleteBuild(buildId)
        }
    }
}
