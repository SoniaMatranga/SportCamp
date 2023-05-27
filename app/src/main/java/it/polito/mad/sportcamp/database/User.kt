package it.polito.mad.sportcamp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id_user: Int? = null,
    @ColumnInfo(name= "nickname") val nickname: String? = null,
    @ColumnInfo(name= "name") val name: String? = null,
    @ColumnInfo(name= "mail") val mail: String? = null,
    @ColumnInfo(name= "city") val city: String? = null,
    @ColumnInfo(name= "age") val age: Int? = null,
    @ColumnInfo(name= "gender") val gender: String? = null,
    @ColumnInfo(name= "level") val level: String? = null,
    @ColumnInfo(name= "sports") val sports: String? = null,
    @ColumnInfo(name= "bio") val bio: String? = null,
    @ColumnInfo(name="image") val image: String? = null
)

