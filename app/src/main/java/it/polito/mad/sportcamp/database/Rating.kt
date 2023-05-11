package it.polito.mad.sportcamp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ratings_table")
data class Rating(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name="id_user") val id_user: Int?,
    @ColumnInfo(name="id_court")val id_court: Int?,
    @ColumnInfo(name="rating") val rating: Int?,
    @ColumnInfo(name="favourite") val favourite: String?
)