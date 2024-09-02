package net.skripsi.downsyndromtracker

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class DownSyndromTrackerApplication : Application() {

    lateinit var database: FirebaseDatabase

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)
    }
}