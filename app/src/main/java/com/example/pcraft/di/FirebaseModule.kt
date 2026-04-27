package com.example.pcraft.di

import android.content.Context
import com.example.pcraft.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseApp(@ApplicationContext context: Context): FirebaseApp? {
        val existing = FirebaseApp.getApps(context).firstOrNull()
        if (existing != null) return existing

        if (!isFirebaseConfigured()) return null

        val options = FirebaseOptions.Builder()
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            .setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET)
            .setGcmSenderId(BuildConfig.FIREBASE_GCM_SENDER_ID)
            .build()

        return FirebaseApp.initializeApp(context, options)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(firebaseApp: FirebaseApp?): FirebaseAuth? {
        return firebaseApp?.let { FirebaseAuth.getInstance(it) }
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(firebaseApp: FirebaseApp?): FirebaseFirestore? {
        return firebaseApp?.let { FirebaseFirestore.getInstance(it) }
    }

    private fun isFirebaseConfigured(): Boolean {
        return BuildConfig.FIREBASE_API_KEY.isNotBlank() &&
            BuildConfig.FIREBASE_APP_ID.isNotBlank() &&
            BuildConfig.FIREBASE_PROJECT_ID.isNotBlank() &&
            BuildConfig.FIREBASE_STORAGE_BUCKET.isNotBlank() &&
            BuildConfig.FIREBASE_GCM_SENDER_ID.isNotBlank()
    }
}
