package com.alhadi.cmms.data.cloud

import android.content.Context
import com.google.firebase.FirebaseApp

/**
 * Offline-first Firebase gateway. Firebase is optional: when google-services.json is absent the
 * default FirebaseApp is never initialized, so [isAvailable] returns false and the app runs purely
 * on the local Room database. When the config is present, cloud auth and Firestore sync activate.
 */
object FirebaseGateway {

    /** True only when a real google-services.json has initialized the default Firebase app. */
    fun isAvailable(context: Context): Boolean =
        runCatching { FirebaseApp.getApps(context).isNotEmpty() }.getOrDefault(false)

    /** Context-free check: succeeds only when a default FirebaseApp exists. */
    fun isAvailable(): Boolean =
        runCatching { FirebaseApp.getInstance() }.isSuccess
}
