package it.polito.mad.sportcamp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id_user: Int?,
    @ColumnInfo(name= "nickname") val nickname: String?,
    @ColumnInfo(name= "name") val name: String?,
    @ColumnInfo(name= "mail") val mail: String?,
    @ColumnInfo(name= "city") val city: String?,
    @ColumnInfo(name= "age") val age: Int?,
    @ColumnInfo(name= "gender") val gender: String?,
    @ColumnInfo(name= "level") val level: String?,
    @ColumnInfo(name= "sports") val sports: String?,
    @ColumnInfo(name= "bio") val bio: String?,
)