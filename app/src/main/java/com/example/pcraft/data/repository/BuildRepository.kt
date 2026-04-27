package com.example.pcraft.data.repository

import android.content.Context
import com.example.pcraft.data.dao.BuildDao
import com.example.pcraft.data.model.BuildConfiguration
import com.example.pcraft.data.model.Component
import com.example.pcraft.data.model.StoreOffer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Singleton
class BuildRepository @Inject constructor(
    @Suppress("UNUSED_PARAMETER") private val buildDao: BuildDao,
    @ApplicationContext context: Context,
    private val authRepository: AuthRepository,
    @Suppress("UNUSED_PARAMETER") private val firestore: FirebaseFirestore?
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val savedBuildsState = MutableStateFlow<List<BuildConfiguration>>(emptyList())
    private val selectedComponentsState = MutableStateFlow<Map<String, Component>>(emptyMap())
    private val selectedStoreOffersState = MutableStateFlow<Map<String, StoreOffer>>(emptyMap())

    val allBuilds: StateFlow<List<BuildConfiguration>> = savedBuildsState
    val selectedComponents: StateFlow<Map<String, Component>> = selectedComponentsState
    val selectedStoreOffers: StateFlow<Map<String, StoreOffer>> = selectedStoreOffersState

    init {
        repositoryScope.launch {
            authRepository.currentUser.collectLatest { user ->
                savedBuildsState.value = if (user == null) {
                    emptyList()
                } else {
                    loadSavedBuilds(user.uid)
                }
            }
        }
    }

    suspend fun insertBuild(build: BuildConfiguration) {
        val userId = authRepository.currentUser.value?.uid ?: return
        val updatedBuilds = listOf(build) + savedBuildsState.value.filterNot { it.id == build.id }
        savedBuildsState.value = updatedBuilds
        saveBuilds(userId, updatedBuilds)
    }

    suspend fun deleteBuild(id: String) {
        val userId = authRepository.currentUser.value?.uid ?: return
        val updatedBuilds = savedBuildsState.value.filterNot { it.id == id }
        savedBuildsState.value = updatedBuilds
        saveBuilds(userId, updatedBuilds)
    }

    fun addComponent(component: Component) {
        selectedComponentsState.value = selectedComponentsState.value.toMutableMap().apply {
            this[component.type.id] = component
        }
    }

    fun removeComponent(typeId: String) {
        selectedComponentsState.value = selectedComponentsState.value.toMutableMap().apply {
            remove(typeId)
        }
        selectedStoreOffersState.value = selectedStoreOffersState.value.filterKeys { it != typeId }
    }

    fun selectStoreOffer(typeId: String, offer: StoreOffer) {
        selectedStoreOffersState.value = selectedStoreOffersState.value.toMutableMap().apply {
            this[typeId] = offer
        }
    }

    fun clearCurrentBuild() {
        selectedComponentsState.value = emptyMap()
        selectedStoreOffersState.value = emptyMap()
    }

    private fun loadSavedBuilds(userId: String): List<BuildConfiguration> {
        val json = prefs.getString(buildsKey(userId), null)
        if (json.isNullOrBlank()) {
            return emptyList()
        }

        val type = object : TypeToken<List<BuildConfiguration>>() {}.type
        return runCatching { gson.fromJson<List<BuildConfiguration>>(json, type) }
            .getOrDefault(emptyList())
    }

    private fun saveBuilds(userId: String, builds: List<BuildConfiguration>) {
        prefs.edit()
            .putString(buildsKey(userId), gson.toJson(builds))
            .apply()
    }

    private fun buildsKey(userId: String): String = "builds_$userId"

    private companion object {
        const val PREFS_NAME = "pcraft_builds"
    }
}
