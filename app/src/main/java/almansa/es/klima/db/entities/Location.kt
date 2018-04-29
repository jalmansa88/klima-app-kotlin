package almansa.es.klima.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Location() {

    constructor(cityName: String, countryCode: String) : this() {
        this.cityName = cityName
        this.countryCode = countryCode
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo
    var cityName: String = ""

    @ColumnInfo
    var countryCode: String = ""

    override fun toString(): String {
        return "Location(id=$id, cityName='$cityName', countryCode='$countryCode')"
    }


}