package com.example.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthManager {

    // Web Client ID Firebase officiel (OAuth 2.0 Web Client)
    const val DEFAULT_WEB_CLIENT_ID = "405168602623-kf2tpr1h9kpfmmk9kg72rmibs7fpt4it.apps.googleusercontent.com"

    fun getWebClientId(context: Context): String {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (resId != 0) {
            try {
                val value = context.getString(resId)
                if (value.isNotBlank()) return value
            } catch (e: Exception) {
                Log.w("AuthManager", "Erreur lors de la lecture de default_web_client_id: ${e.message}")
            }
        }
        return DEFAULT_WEB_CLIENT_ID
    }

    fun attemptAutoSignIn(
        context: Context,
        credentialManager: CredentialManager,
        onAuthSuccess: () -> Unit,
        onUnauthenticated: () -> Unit,
        scope: CoroutineScope
    ) {
        if (Firebase.auth.currentUser != null) {
            onAuthSuccess()
            return
        }

        val clientId = getWebClientId(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    Firebase.auth.signInWithCredential(authCredential).await()
                    onAuthSuccess()
                } else {
                    onUnauthenticated()
                }
            } catch (e: Exception) {
                onUnauthenticated()
            }
        }
    }

    fun onGoogleSignInClicked(
        activity: Activity,
        credentialManager: CredentialManager,
        onAuthSuccess: () -> Unit,
        onAuthError: (String) -> Unit,
        onAuthCancelled: () -> Unit,
        scope: CoroutineScope
    ) {
        val clientId = getWebClientId(activity)

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                    Log.i("AuthManager", "Connexion Firebase réussie: UID=${authResult.user?.uid}, Email=${authResult.user?.email}")
                    onAuthSuccess()
                } else {
                    onAuthError("Type d'identifiant reçu non compatible (${credential::class.java.simpleName}).")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("AuthManager", "GetCredentialCancellationException: ${e.message}", e)
                val rawMsg = e.message ?: ""
                when {
                    rawMsg.contains("10") || rawMsg.contains("DEVELOPER_ERROR") -> {
                        onAuthError("Erreur Google 10 (Signature SHA-1) : L'APK actuel n'a pas la signature du debug.keystore officiel Firebase. Vous pouvez entrer directement en Mode Local ou recompiler avec le keystore.")
                    }
                    rawMsg.contains("12500") -> {
                        onAuthError("Erreur Google 12500 : Configuration Google Play Services requise. Vous pouvez entrer directement en Mode Local.")
                    }
                    rawMsg.contains("7") || rawMsg.contains("NETWORK", ignoreCase = true) -> {
                        onAuthError("Erreur réseau : Vérifiez votre connexion Internet ou entrez en Mode Local.")
                    }
                    rawMsg.contains("16") -> {
                        onAuthError("Sélection de compte Google interrompue. Vous pouvez réessayer ou entrer en Mode Local.")
                    }
                    rawMsg.isBlank() || rawMsg.contains("cancelled", ignoreCase = true) || rawMsg.contains("canceled", ignoreCase = true) -> {
                        onAuthCancelled()
                    }
                    else -> {
                        onAuthError("Connexion interrompue ($rawMsg). Vous pouvez entrer en Mode Local.")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthManager", "Échec connexion Google", e)
                val msg = when {
                    e.message?.contains("10") == true -> "Erreur Google 10 (SHA-1 non reconnu). Vous pouvez entrer en Mode Local."
                    e.message?.contains("7") == true -> "Erreur réseau. Vérifiez votre connexion Internet ou utilisez le Mode Local."
                    else -> e.localizedMessage ?: "Échec de connexion."
                }
                onAuthError(msg)
            }
        }
    }

    fun signOut(
        credentialManager: CredentialManager,
        scope: CoroutineScope,
        onSignOutComplete: () -> Unit
    ) {
        scope.launch {
            withContext(NonCancellable) {
                try {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {
                    Log.w("AuthManager", "Impossible d'effacer les identifiants locaux: ${e.message}")
                } finally {
                    try {
                        Firebase.auth.signOut()
                    } catch (e: Exception) {
                        Log.w("AuthManager", "Erreur Firebase.auth.signOut: ${e.message}")
                    }
                    onSignOutComplete()
                }
            }
        }
    }
}
