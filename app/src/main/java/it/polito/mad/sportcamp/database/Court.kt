package it.polito.mad.sportcamp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courts_table")
data class Court(
    @PrimaryKey(autoGenerate = true) val id_court: Int? = null,
    @ColumnInfo(name="court_name") val court_name: String?  = null,
    @ColumnInfo(name="address")val address: String?  = null,
    @ColumnInfo(name="city") val city: String?  = null,
    @ColumnInfo(name="sport") val sport: String?  = null,
    @ColumnInfo(name= "court_rating") val court_rating: Float?  = null,
    @ColumnInfo(name="image") val image: String?  = null,
)