package almansa.es.klima.db

import almansa.es.klima.db.dao.LocationDao
import almansa.es.klima.db.entities.Location
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [Location::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun locationDao(): LocationDao
}