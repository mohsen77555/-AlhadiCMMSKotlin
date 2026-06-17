package com.alhadi.cmms

import android.app.Application
import com.alhadi.cmms.data.AppDatabase
import com.alhadi.cmms.data.CmmsRepository
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
        val database = AppDatabase.getDatabase(this)
        repository = CmmsRepository(database)
        applicationScope.launch {
            repository.seedSampleData(replace = false)
        }
    }
}
