package it.polito.mad.sportcamp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations_table")
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id_reservation: Int?,
    @ColumnInfo(name= "id_user") val id_user: Int?,
    @ColumnInfo(name= "id_court") val id_court: Int?,
    @ColumnInfo(name= "time_slot") val time_slot: String?,
    @ColumnInfo(name= "date") val date: String?,
    @ColumnInfo(name= "equipments") val equipments: String?,
    @ColumnInfo(name= "options")val options: String?
)