package it.polito.mad.sportcamp

import android.app.Application
import it.polito.mad.sportcamp.database.AppDatabase

class SportCampApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
