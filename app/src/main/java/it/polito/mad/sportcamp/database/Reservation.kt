package it.polito.mad.sportcamp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations_table")
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id_reservation: Int? = null,
    @ColumnInfo(name= "id_user") val id_user: Int? = null,
    @ColumnInfo(name= "id_court") val id_court: Int? = null,
    @ColumnInfo(name= "id_time_slot") val id_time_slot: Int? = null,
    @ColumnInfo(name= "date") val date: String? = null,
    @ColumnInfo(name= "equipments") val equipments: String? = null,
    @ColumnInfo(name= "options")val options: String? = null
) {
}