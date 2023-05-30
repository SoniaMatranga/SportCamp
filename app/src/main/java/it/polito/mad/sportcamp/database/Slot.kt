package it.polito.mad.sportcamp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "time_slots_table")
data class TimeSlot (
    @PrimaryKey(autoGenerate = true) val id_time_slot: Int? = null,
    @ColumnInfo(name= "time_slot") val time_slot: String? = null,
)