package it.polito.mad.sportcamp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courts_table")
data class Court(
    @PrimaryKey(autoGenerate = true) val id_court: Int?,
    @ColumnInfo(name="court_name") val court_name: String?,
    @ColumnInfo(name="address")val address: String?,
    @ColumnInfo(name="city") val city: String?,
    @ColumnInfo(name="sport") val sport: String?,
    @ColumnInfo(name= "court_rating") val court_rating: Int?,
    @ColumnInfo(name="image") val image: String?
)