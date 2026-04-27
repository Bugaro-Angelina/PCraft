package com.example.pcraft.data.repository

import android.content.Context
import com.example.pcraft.data.model.AuthUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext context: Context,
    @Suppress("UNUSED_PARAMETER") private val firestore: FirebaseFirestore?
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow(loadSavedSession())
    val currentUser: StateFlow<AuthUser?> = _currentUser

    val isConfigured: Boolean
        get() = true

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return runCatching {
            val cleanEmail = email.trim()
            val user = loadUsers().firstOrNull { it.email.equals(cleanEmail, ignoreCase = true) }
                ?: error("Аккаунт не найден")

            if (user.password != password) {
                error("Неверный пароль")
            }

            saveSession(user.uid, user.email)
            _currentUser.value = AuthUser(uid = user.uid, email = user.email)
        }
    }

    suspend fun signUp(email: String, password: String): Result<Unit> {
        return runCatching {
            val cleanEmail = email.trim()
            val users = loadUsers()

            if (users.any { it.email.equals(cleanEmail, ignoreCase = true) }) {
                error("Пользователь с таким email уже существует")
            }

            val newUser = StoredUser(
                uid = UUID.randomUUID().toString(),
                email = cleanEmail,
                password = password
            )

            saveUsers(users + newUser)
            saveSession(newUser.uid, newUser.email)
            _currentUser.value = AuthUser(uid = newUser.uid, email = newUser.email)
        }
    }

    fun signOut() {
        prefs.edit()
            .remove(KEY_SESSION_UID)
            .remove(KEY_SESSION_EMAIL)
            .apply()
        _currentUser.value = null
    }

    private fun loadSavedSession(): AuthUser? {
        val uid = prefs.getString(KEY_SESSION_UID, null) ?: return null
        val email = prefs.getString(KEY_SESSION_EMAIL, null) ?: return null
        return AuthUser(uid = uid, email = email)
    }

    private fun loadUsers(): List<StoredUser> {
        val rawJson = prefs.getString(KEY_USERS_JSON, "[]").orEmpty()
        val array = JSONArray(rawJson)
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                add(
                    StoredUser(
                        uid = item.optString("uid"),
                        email = item.optString("email"),
                        password = item.optString("password")
                    )
                )
            }
        }
    }

    private fun saveUsers(users: List<StoredUser>) {
        val array = JSONArray()
        users.forEach { user ->
            array.put(
                JSONObject()
                    .put("uid", user.uid)
                    .put("email", user.email)
                    .put("password", user.password)
            )
        }
        prefs.edit().putString(KEY_USERS_JSON, array.toString()).apply()
    }

    private fun saveSession(uid: String, email: String) {
        prefs.edit()
            .putString(KEY_SESSION_UID, uid)
            .putString(KEY_SESSION_EMAIL, email)
            .apply()
    }

    private data class StoredUser(
        val uid: String,
        val email: String,
        val password: String
    )

    private companion object {
        const val PREFS_NAME = "pcraft_auth"
        const val KEY_USERS_JSON = "users_json"
        const val KEY_SESSION_UID = "session_uid"
        const val KEY_SESSION_EMAIL = "session_email"
    }
}

