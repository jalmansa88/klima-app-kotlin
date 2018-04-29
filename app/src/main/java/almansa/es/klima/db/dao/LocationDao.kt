package almansa.es.klima.db.dao

import almansa.es.klima.db.entities.Location
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE

@Dao
interface LocationDao {

    @Insert(onConflict = REPLACE)
    fun insert(location: Location)

    @Query("SELECT * FROM location")
    fun getAll() : List<Location>

    @Delete
    fun delete(location: Location)
}