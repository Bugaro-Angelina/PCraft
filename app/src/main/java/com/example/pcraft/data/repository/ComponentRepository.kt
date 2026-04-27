package com.example.pcraft.data.repository

import android.content.Context
import com.example.pcraft.data.MockDataProvider
import com.example.pcraft.data.dao.ComponentDao
import com.example.pcraft.data.model.Component
import com.example.pcraft.data.model.StoreOffer
import com.example.pcraft.data.remote.awaitCompletion
import com.example.pcraft.data.remote.awaitResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONArray

@Singleton
class ComponentRepository @Inject constructor(
    @Suppress("UNUSED_PARAMETER") private val componentDao: ComponentDao,
    @ApplicationContext context: Context,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore?
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val componentsState = MutableStateFlow<List<Component>>(emptyList())
    private val storeOffersState = MutableStateFlow(MockDataProvider.storeOffers)

    val allComponents: StateFlow<List<Component>> = componentsState

    val favoriteComponents: Flow<List<Component>> = componentsState.map { components ->
        components.filter { it.isFavorite }
    }

    init {
        repositoryScope.launch {
            loadCatalog()
            authRepository.currentUser.collectLatest { user ->
                syncFavorites(user?.uid)
            }
        }
    }

    fun getStoreOffersForComponent(componentId: String): List<StoreOffer> {
        return storeOffersState.value.filter { it.componentId == componentId }
    }

    suspend fun toggleFavorite(component: Component) {
        val currentFavorites = loadFavoriteIds(authRepository.currentUser.value?.uid)
        val updatedFavorites = if (component.id in currentFavorites) {
            currentFavorites - component.id
        } else {
            currentFavorites + component.id
        }

        saveFavoriteIds(authRepository.currentUser.value?.uid, updatedFavorites)
        applyFavorites(updatedFavorites)

        val userId = authRepository.currentUser.value?.uid ?: return
        val firestoreInstance = firestore ?: return
        val document = firestoreInstance.collection("users")
            .document(userId)
            .collection("favorites")
            .document(component.id)

        if (component.id in updatedFavorites) {
            document.set(mapOf("componentId" to component.id)).awaitCompletion()
        } else {
            document.delete().awaitCompletion()
        }
    }

    suspend fun getComponent(componentId: String): Component? {
        return componentsState.value.firstOrNull { it.id == componentId }
    }

    suspend fun populateDatabase() {
        if (componentsState.value.isEmpty()) {
            loadCatalog()
        }
        syncFavorites(authRepository.currentUser.value?.uid)
    }

    private suspend fun loadCatalog() {
        storeOffersState.value = MockDataProvider.storeOffers
        componentsState.value = applyFavoriteIds(
            MockDataProvider.components,
            loadFavoriteIds(authRepository.currentUser.value?.uid)
        )

        val firestoreInstance = firestore ?: return

        val remoteComponentsCount = runCatching {
            firestoreInstance.collection(CATALOG_COMPONENTS_COLLECTION)
                .get()
                .awaitResult()
                .size()
        }.getOrDefault(0)

        val remoteOffersCount = runCatching {
            firestoreInstance.collection(CATALOG_OFFERS_COLLECTION)
                .get()
                .awaitResult()
                .size()
        }.getOrDefault(0)

        if (remoteComponentsCount == 0 || remoteOffersCount == 0) {
            seedRemoteCatalog(firestoreInstance)
        }
    }

    private suspend fun seedRemoteCatalog(firestoreInstance: FirebaseFirestore) {
        MockDataProvider.components.forEach { component ->
            firestoreInstance.collection(CATALOG_COMPONENTS_COLLECTION)
                .document(component.id)
                .set(RemoteCatalogEntry(component.id, gson.toJson(component.copy(isFavorite = false))))
                .awaitCompletion()
        }

        MockDataProvider.storeOffers.forEach { offer ->
            firestoreInstance.collection(CATALOG_OFFERS_COLLECTION)
                .document(offer.id)
                .set(RemoteCatalogEntry(offer.id, gson.toJson(offer)))
                .awaitCompletion()
        }
    }

    private suspend fun syncFavorites(userId: String?) {
        val firestoreInstance = firestore
        val favoriteIds = if (userId != null && firestoreInstance != null) {
            val remoteFavoriteIds = runCatching {
                firestoreInstance.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .get()
                    .awaitResult()
                    .documents
                    .map { it.id }
                    .toSet()
            }.getOrDefault(emptySet())

            if (remoteFavoriteIds.isNotEmpty()) {
                saveFavoriteIds(userId, remoteFavoriteIds)
                remoteFavoriteIds
            } else {
                loadFavoriteIds(userId)
            }
        } else {
            loadFavoriteIds(userId)
        }

        applyFavorites(favoriteIds)
    }

    private fun applyFavorites(favoriteIds: Set<String>) {
        componentsState.value = applyFavoriteIds(MockDataProvider.components, favoriteIds)
    }

    private fun applyFavoriteIds(components: List<Component>, favoriteIds: Set<String>): List<Component> {
        return components.map { component ->
            component.copy(isFavorite = component.id in favoriteIds)
        }
    }

    private fun loadFavoriteIds(userId: String?): Set<String> {
        val key = favoriteKey(userId)
        val storedJson = prefs.getString(key, null)
        if (storedJson != null) {
            val array = JSONArray(storedJson)
            return buildSet {
                for (index in 0 until array.length()) {
                    add(array.optString(index))
                }
            }.filter { it.isNotBlank() }.toSet()
        }

        return if (userId == null) {
            MockDataProvider.components.filter { it.isFavorite }.map { it.id }.toSet()
        } else {
            emptySet()
        }
    }

    private fun saveFavoriteIds(userId: String?, favorites: Set<String>) {
        val array = JSONArray()
        favorites.forEach { array.put(it) }
        prefs.edit().putString(favoriteKey(userId), array.toString()).apply()
    }

    private fun favoriteKey(userId: String?): String {
        return if (userId.isNullOrBlank()) {
            "favorites_guest"
        } else {
            "favorites_$userId"
        }
    }

    private data class RemoteCatalogEntry(
        val id: String = "",
        val payloadJson: String = ""
    )

    private companion object {
        const val PREFS_NAME = "pcraft_catalog"
        const val CATALOG_COMPONENTS_COLLECTION = "catalog_components"
        const val CATALOG_OFFERS_COLLECTION = "catalog_offers"
    }
}
