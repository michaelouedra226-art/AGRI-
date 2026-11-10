package com.example

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class AgriApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ensureFirebase(this)
    }

    companion object {
        fun ensureFirebase(context: Context) {
            try {
                if (FirebaseApp.getApps(context).isEmpty()) {
                    Log.i("AgriApp", "FirebaseApp non initialisé automatiquement. Initialisation avec les options de repli...")
                    val options = FirebaseOptions.Builder()
                        .setApplicationId("1:405168602623:android:a2947fef57f9c056593bca")
                        .setApiKey("AIzaSyCN-4GwEy7g57lu2g8HiZNch_LugRy-tFM")
                        .setProjectId("gen-lang-client-0190245043")
                        .setStorageBucket("gen-lang-client-0190245043.firebasestorage.app")
                        .setGcmSenderId("405168602623")
                        .build()
                    FirebaseApp.initializeApp(context, options)
                    Log.i("AgriApp", "FirebaseApp initialisé avec succès.")
                } else {
                    Log.i("AgriApp", "FirebaseApp déjà initialisé.")
                }
            } catch (e: Exception) {
                Log.e("AgriApp", "Impossible d'initialiser FirebaseApp: ${e.message}", e)
            }
        }
    }
}
