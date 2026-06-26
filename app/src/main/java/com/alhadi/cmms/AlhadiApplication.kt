package com.alhadi.cmms

import android.app.Application
import android.util.Log
import com.alhadi.cmms.data.AppDatabase
import com.alhadi.cmms.data.CmmsRepository
import com.alhadi.cmms.data.*
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AlhadiApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var repository: CmmsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase from google-services.json (no-op/null if it isn't bundled).
        val firebaseApp = FirebaseApp.initializeApp(this)
        Log.i("AlhadiCMMS", "Firebase connected: ${firebaseApp != null} (${firebaseApp?.options?.projectId ?: "offline"})")

        val database = AppDatabase.getDatabase(this)
        repository = CmmsRepository(database)
        // Start the Firestore down-sync (pull) for the whole app lifetime.
        repository.startCloudSync(applicationScope)
        applicationScope.launch {
            repository.seedSampleData(replace = false)
        }
    }
}
